package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;

public class ParkourPlayerManager implements Listener {

	private ExtremeParkourPlugin plugin;
	private HashMap<UUID, ParkourPlayer> players;
	
	public ParkourPlayerManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		players = new HashMap<UUID, ParkourPlayer>();
	}
	
	public void disable() {
		
	}
	
	//TODO start parkour, end parkour (with message) methods
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Block b = e.getPlayer().getLocation().getBlock();
		if (b == null) {
			return;
		}
		if (b.getType() == Material.GOLD_PLATE || b.getType() == Material.IRON_PLATE || b.getType() == Material.WOOD_PLATE) {
			Point p = plugin.getParkourManager().getPoint(b);
			if (p != null) {
				//TODO later move the endpoint source to protocol lib
				prossesPoint(e.getPlayer(), p);
			}
		}
	}
	
	private void prossesPoint(Player p, Point point) {
		if (point instanceof Startpoint) {
			//TODO start
		} else if (point instanceof Endpoint) {
			//TODO end
		} else if (point instanceof Checkpoint) {
			//TODO check
		} else {
			//TODO debug
		}
	}
	
}
