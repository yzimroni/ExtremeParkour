package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
import net.yzimroni.extremeparkour.utils.Utils;

public class ParkourPlayerManager implements Listener {

	private ExtremeParkourPlugin plugin;
	private Events events;
	private HashMap<UUID, ParkourPlayer> players;
	
	public ParkourPlayerManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
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
	
	public boolean isPakouring(Player p) {
		return players.containsKey(p.getUniqueId());
	}
	
	public ParkourPlayer getPlayer(Player p) {
		return players.get(p.getUniqueId());
	}
	
	public boolean startParkour(Player p, Parkour parkour) {
		if (!parkour.isComplete()) {
			return false;
		}
		
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
			playerp.setLastMessage();
			playerp.setStartTime(System.currentTimeMillis());
			return true;
		} else {
			ParkourPlayer playerp = players.get(p.getUniqueId());
			if (parkour.equals(playerp.getParkour())) {
				if (!playerp.checkAndSetLastMessage()) {
					return false;
				}
				playerp.setLastCheckpoint(-1);
				playerp.setLastCheckpointTime(0);
				p.sendMessage(ChatColor.GREEN + "Time reset to " + Utils.formatTime(0));
			} else {
				leaveParkour(p, "");
				return startParkour(p, parkour);
			}
		}
		return false;
	}
	
	public boolean leaveParkour(Player p, String reason) {
		if (players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = getPlayer(p);
			players.remove(p.getUniqueId());
			if (reason != null && !reason.isEmpty()) {
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + reason);
			}
			return true;
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
				playerp.setLastMessage();
				return true;
			} else {
				if (!playerp.checkAndSetLastMessage()) {
					return false;
				}
				if ((playerp.getLastCheckpoint() + 1) < check.getIndex()) {
					p.sendMessage(ChatColor.RED + "You need to get " + check.getParkour().getCheckpoint(playerp.getLastCheckpoint() + 1).getName() + " first");
				} else {
					p.sendMessage(ChatColor.GREEN + "You already got this checkpoint"); //TODO
				}
			}
		}
		return false;
	}
	
	public boolean teleportStart(Player p) {
		if (isPakouring(p)) {
			ParkourPlayer playerp = getPlayer(p);
			playerp.setTeleportAllowed(true);
			p.teleport(playerp.getParkour().getStartPoint().getLocation());
			p.sendMessage(ChatColor.GREEN + "Teleported back to " + playerp.getParkour().getStartPoint().getName());
			return true;
		} else {
			p.sendMessage(ChatColor.RED + "You aren't in a parkour");
			return false;
		}
	}
	
	public boolean teleportLatestCheckpoint(Player p) {
		if (isPakouring(p)) {
			ParkourPlayer playerp = getPlayer(p);
			Point latest = null;
			if (playerp.getLastCheckpoint() == -1) {
				latest = playerp.getParkour().getStartPoint();
			} else {
				latest = playerp.getParkour().getCheckpoint(playerp.getLastCheckpoint());
			}
			playerp.setTeleportAllowed(true);
			p.teleport(latest.getLocation().getBlock().getLocation().add(0.5, 1.5, 0.5));
			p.sendMessage(ChatColor.GREEN + "Teleported back to " + latest.getName());
			return true;
		} else {
			p.sendMessage(ChatColor.RED + "You aren't in a parkour");
			return false;
		}
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
					if (!playerp.checkAndSetLastMessage()) {
						return false;
					}
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
		ParkourPlayerScore old = plugin.getData().getBestPlayerScore(p, parkour);
		ParkourPlayerScore now = new ParkourPlayerScore(p.getUniqueId(), parkour.getId(), playerp.getStartTime(), time);
		players.remove(p.getUniqueId());
		// TODO player score
		p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You completed the parkour in " + ChatColor.GREEN + Utils.formatTime(time));
		if (old != null) {
			if (now.getTimeTook() < old.getTimeTook()) {
				p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You break your previous record of " + ChatColor.RESET + "" + ChatColor.AQUA + Utils.formatTime(old.getTimeTook()) + ChatColor.GOLD + "" + ChatColor.BOLD + " (Improvement of " + ChatColor.GREEN + Utils.formatTime(old.getTimeTook() - now.getTimeTook()) + ChatColor.GOLD + "" + ChatColor.BOLD + ")!");
			}
		}
		plugin.getData().insertPlayerScore(now);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				plugin.getParkourManager().initLeaderboard(parkour);
			}
		});
		return true;
	}
	
	

}