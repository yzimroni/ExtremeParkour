package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.Utils;

public class ParkourPlayerManager implements Listener {

	private ExtremeParkourPlugin plugin;
	private Events events;
	private HashMap<UUID, ParkourPlayer> players;

	private boolean protocolLib = false;
	private boolean remove = true;

	public ParkourPlayerManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		events = new Events(plugin, this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
		players = new HashMap<UUID, ParkourPlayer>();

		initProtocolLib();
		initBarSend();
	}

	public void disable() {
		remove = false;
		for (ParkourPlayer p : players.values()) {
			p.leaveParkour("Plugin disabled");
		}
		players.clear();
		remove = true;
	}

	private void initProtocolLib() {
		if (Utils.checkPlugin("ProtocolLib")) {
			System.out.println("ProtcolLib found, using it");
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST,
					PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK) {

				@Override
				public void onPacketReceiving(PacketEvent e) {
					if (isPakouring(e.getPlayer())) {
						ParkourPlayer parkourPlayer = getPlayer(e.getPlayer());
						if (parkourPlayer == null) {
							return;
						}
						parkourPlayer.sendBar();
						// Point point =
						// ParkourPlayerManager.this.plugin.getParkourManager().getPoint(e.getPlayer().getLocation().getBlock());
						PacketContainer p = e.getPacket();
						Point point = ParkourPlayerManager.this.plugin.getParkourManager()
								.getPoint(new Location(e.getPlayer().getWorld(), p.getDoubles().read(0),
										p.getDoubles().read(1), p.getDoubles().read(2)).getBlock(), false);
						if (point != null && point instanceof Endpoint) {
							parkourPlayer.checkComplete((Endpoint) point);
						}

					}
				}
			});
			protocolLib = true;
		} else {
			System.out.println("ProtocolLib doesn't found");
		}
	}

	@SuppressWarnings("deprecation")
	private void initBarSend() {
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				for (ParkourPlayer p : players.values()) {
					if (System.currentTimeMillis() - p.getLastActionbar() > 50) {
						p.sendBar();
					}
				}
			}
		}, 1L, 1L);
	}

	public List<ParkourPlayer> getParkourPlayers(Parkour parkour) {
		List<ParkourPlayer> list = new ArrayList<ParkourPlayer>();

		for (ParkourPlayer player : players.values()) {
			if (player.getParkour().equals(parkour)) {
				list.add(player);
			}
		}

		return list;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Block b = e.getPlayer().getLocation().getBlock();
		if (b == null) {
			return;
		}
		if (plugin.getParkourManager().isGeneralParkourBlock(b)) {
			Point p = plugin.getParkourManager().getPoint(b, false);
			if (p != null) {
				if (!(p instanceof Endpoint) || !protocolLib) {
					ParkourPlayer parkourPlayer = getPlayer(e.getPlayer());
					if (parkourPlayer == null) {
						if (p instanceof Startpoint) {
							startParkour(e.getPlayer(), p.getParkour());
							return;
						}
						return;
					}
					parkourPlayer.processPoint(p);
				}
			}
		}
	}

	public boolean isPakouring(Player p) {
		return players.containsKey(p.getUniqueId());
	}

	public ParkourPlayer getPlayer(Player p) {
		return players.get(p.getUniqueId());
	}

	protected void removePlayer(UUID u) {
		if (!remove) {
			return;
		}
		players.remove(u);
	}

	public ParkourPlayer startParkour(Player p, Parkour parkour) {
		if (!parkour.isComplete()) {
			return null;
		}
		ParkourPlayer playerp = null;
		if (!players.containsKey(p.getUniqueId())) {
			playerp = new ParkourPlayer(p.getUniqueId(), parkour);
			players.put(p.getUniqueId(), playerp);
		} else {
			playerp = players.get(p.getUniqueId());
		}
		playerp.processPoint(parkour.getStartPoint());

		return playerp;
	}

}
