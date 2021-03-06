package net.yzimroni.extremeparkour.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Actionbar {
	private Plugin plugin;
	private HashMap<UUID, String> messages;

	// Actionbar nms
	private Method createChatComponent;
	private Constructor<?> createChatPacket;

	// Player packet sending nms
	private boolean playerNMS = false;
	private Method playerHandle;
	private Field playerConnection;
	private Method sendPacket;

	public Actionbar(Plugin p) {
		plugin = p;

		try {
			createChatComponent = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a",
					String.class);
			createChatPacket = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"),
					byte.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		messages = new HashMap<UUID, String>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				sendBars();

			}
		}, 20, 20);
	}

	public void sendActionBarRaw(Player p, String s) {
		try {
			Object text = createChatComponent.invoke(null, "{\"text\": \"" + s + "\"}");
			Object actionbar = createChatPacket.newInstance(text, (byte) 2);
			sendPacket(p, actionbar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendBars() {
		if (messages.isEmpty())
			return;
		for (Entry<UUID, String> i : messages.entrySet()) {
			Player p = Bukkit.getPlayer(i.getKey());
			if (p == null) {
				messages.remove(i.getKey());
				continue;
			}
			sendActionBarRaw(p, i.getValue());
		}
	}

	public boolean hasActionBar(Player p) {
		return messages.containsKey(p.getUniqueId());
	}

	public String getActionBar(Player p) {
		return messages.get(p.getUniqueId());
	}

	public void removeActionBar(Player p) {
		messages.remove(p.getUniqueId());
	}

	public void sendActionBar(Player p, String s) {
		if (s == null || s.isEmpty()) {
			removeActionBar(p);
			return;
		}
		if (hasActionBar(p)) {
			removeActionBar(p);
		}
		messages.put(p.getUniqueId(), s);
		sendActionBarRaw(p, s);
	}

	private Class<?> getNMSClass(String classname) {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = "net.minecraft.server." + version + classname;
		Class<?> nmsClass = null;
		try {
			nmsClass = Class.forName(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nmsClass;
	}

	private void sendPacket(Player player, Object packet) {
		initPlayerNMS(player);
		try {
			Object handle = playerHandle.invoke(player);
			Object playerConnection = this.playerConnection.get(handle);
			sendPacket.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void initPlayerNMS(Player player) {
		if (!playerNMS) {
			try {
				playerHandle = player.getClass().getMethod("getHandle");
				Object handle = playerHandle.invoke(player);
				playerConnection = handle.getClass().getField("playerConnection");
				Object playerConnection = this.playerConnection.get(handle);
				sendPacket = playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			playerNMS = true;
		}
	}

}
