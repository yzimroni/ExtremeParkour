package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
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
		if (b.getType() == Startpoint.MATERIAL.getMaterial() || b.getType() == Checkpoint.MATERIAL.getMaterial() || b.getType() == Endpoint.MATERIAL.getMaterial()) {
			Point p = plugin.getParkourManager().getPoint(b);
			if (p != null) {
				//TODO later move the endpoint source to protocol lib
				prossesPoint(e.getPlayer(), p);
			}
		}
	}
	
	private void prossesPoint(Player p, Point point) {
		if (point instanceof Startpoint) {
			startParkour(p, point.getParkour());
		} else if (point instanceof Endpoint) {
			checkComplete(p, point.getParkour());
		} else if (point instanceof Checkpoint) {
			setCheckpoint(p, (Checkpoint) point);
		} else {
			//TODO debug
		}
	}
	
	public boolean startParkour(Player p, Parkour parkour) {
		if (p.isFlying()) {
			return false;
		}
		
		if (p.getGameMode() == GameMode.SPECTATOR) {
			return false;
		}
		if (!players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = new ParkourPlayer(p.getUniqueId(), parkour, System.currentTimeMillis());
			playerp.setLastCheckpoint(-1); //So the next checkpoint is index 0
			players.put(p.getUniqueId(), playerp);
			p.sendMessage(ChatColor.GREEN + "You start the parkour!");
			playerp.setStartTime(System.currentTimeMillis());
			return true;
		} else {
			ParkourPlayer playerp = players.get(p.getUniqueId());
			if (parkour.equals(playerp.getParkour())) {
				p.sendMessage("restart the parkour WIP");
				//TODO restart the parkour
			} else {
				p.sendMessage("not the same parkour WIP");
				//TODO send message
			}
		}
		return false;
	}
	
	public boolean setCheckpoint(Player p, Checkpoint check) {
		if (players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = players.get(p.getUniqueId());
			if (!check.getParkour().equals(playerp.getParkour())) {
				return false;
			}
			//If this is the next checkpoint for the player
			if ((playerp.getLastCheckpoint() + 1) == check.getIndex()) {
				playerp.setLastCheckpoint(check.getIndex());
				playerp.setLastCheckpointTime(System.currentTimeMillis() - playerp.getStartTime());
				p.sendMessage(ChatColor.YELLOW + "You are now on checkpoint " + ChatColor.AQUA + "#" + (check.getDisplayIndex()));
				return true;
			} else {
				p.sendMessage("Not this one WIP");
			}
		}
		return false;
	}
	
	public boolean checkComplete(Player p, Parkour parkour) {
		if (players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = players.get(p.getUniqueId());
			if (!parkour.equals(playerp.getParkour())) {
				return false;
			}
			if (parkour.hasCheckpoints()) {
				if (playerp.getLastCheckpoint() == (parkour.getChestpointsCount() - 1)) {
					// The player was in all the checkpoint and the latest one
					// is the last one
					return completeParkour(p, parkour, playerp);
				} else {
					p.sendMessage(ChatColor.RED + "You missed a checkpoint");
				}
			} else {
				return completeParkour(p, parkour, playerp);
			}

		}
		return false;
	}
	
	private boolean completeParkour(Player p, Parkour parkour, ParkourPlayer playerp) {
		long time = System.currentTimeMillis() - playerp.getStartTime();
		players.remove(p.getUniqueId());
		// TODO player score
		p.sendMessage(ChatColor.GREEN + "You completed the parkour in " + ((double) time / 1000) + " seconds WIP");
		return false;
	}
	
	

}
