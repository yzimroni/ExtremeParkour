package net.yzimroni.extremeparkour.utils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Actionbar {
	private Plugin plugin;
	private HashMap<UUID, String> messages;

	public Actionbar(Plugin p) {
		plugin = p;
		messages = new HashMap<UUID, String>();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				sendBars();

			}
		}, 20, 20);
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

	public void onDisable() {
		messages.clear();
		messages = null;
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

	public void sendActionBarRaw(Player p, String s) {
		try {
			Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
					.invoke(null, "{\"text\": \"" + s + "\"}");
			Constructor<?> titleConstructor = getNMSClass("PacketPlayOutChat")
					.getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
			Object packet = titleConstructor.newInstance(chatTitle, (byte) 2);
			sendPacket(p, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Class<?> getNMSClass(String classname) {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = "net.minecraft.server." + version + classname;
		Class<?> nmsClass = null;
		try {
			nmsClass = Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return nmsClass;
	}

	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
