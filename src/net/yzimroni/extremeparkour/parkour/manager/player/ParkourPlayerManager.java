package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourComplete;
import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourFailed;
import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourStart;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.PointEffect;
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
			leaveParkour(p.getBukkitPlayer(), "Plugin disabled");
		}
		players.clear();
		remove = true;
	}
	
	private void initProtocolLib() {
		if (Utils.checkPlugin("ProtocolLib")) {
			System.out.println("ProtcolLib found, using it");
			ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK) {
				
				@Override
				public void onPacketReceiving(PacketEvent e) {
					if (isPakouring(e.getPlayer())) {
						sendBar(e.getPlayer());
						//Point point = ParkourPlayerManager.this.plugin.getParkourManager().getPoint(e.getPlayer().getLocation().getBlock());
						PacketContainer p = e.getPacket();
						Point point = ParkourPlayerManager.this.plugin.getParkourManager().getPoint(
								new Location(e.getPlayer().getWorld(), p.getDoubles().read(0), p.getDoubles().read(1), p.getDoubles().read(2)).getBlock(), false);
						if (point != null && point instanceof Endpoint) {
							processPoint(e.getPlayer(), point);
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
						sendBar(Bukkit.getPlayer(p.getPlayer()));
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
					processPoint(e.getPlayer(), p);
				}
			}
		}
	}
	
	private void processPoint(Player p, Point point) {
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
	
	public ParkourPlayer startParkour(Player p, Parkour parkour) {
		if (!parkour.isComplete()) {
			return null;
		}
		
		if (p.isFlying()) {
			return null;
		}
		
		if (p.getGameMode() == GameMode.SPECTATOR) {
			return null;
		}
		if (!players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = new ParkourPlayer(p.getUniqueId(), parkour, System.currentTimeMillis());
			playerp.setLastCheckpoint(-1); //So the next checkpoint is index 0
			players.put(p.getUniqueId(), playerp);
		    for (PotionEffect effect : p.getActivePotionEffects()){
		    	playerp.getEffectsBefore().add(effect);
		    	p.removePotionEffect(effect.getType());
		    }
		    p.sendMessage(ChatColor.GREEN + "You start the parkour!");
			playerp.setLastMessage();
			playerp.setStartTime(System.currentTimeMillis());
			handlePointEffect(p, parkour.getStartPoint());
			Bukkit.getPluginManager().callEvent(new PlayerParkourStart(parkour, playerp, p));
			return playerp;
		} else {
			ParkourPlayer playerp = players.get(p.getUniqueId());
			if (parkour.equals(playerp.getParkour())) {
				if (!playerp.checkAndSetLastMessage()) {
					return null;
				}
			    for (PotionEffect effect : p.getActivePotionEffects()){
			    	p.removePotionEffect(effect.getType()); //The potion is from the parkour
			    }
			    playerp.getEffectsAdded().clear();
				playerp.setLastCheckpoint(-1);
				playerp.setLastCheckpointTime(0);
				playerp.setStartTime(System.currentTimeMillis());
				p.sendMessage(ChatColor.GREEN + "Time reset to " + Utils.formatTime(0));
				handlePointEffect(p, parkour.getStartPoint());
			} else {
				leaveParkour(p, "");
				return startParkour(p, parkour);
			}
		}
		return null;
	}
	
	public boolean leaveParkour(Player p, String reason) {
		if (players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = getPlayer(p);
			if (remove) {
				players.remove(p.getUniqueId());
			}
			if (reason != null && !reason.isEmpty()) {
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + reason);
			}
			for (PotionEffect before : playerp.getEffectsBefore()) {
				p.addPotionEffect(before);
			}
			playerp.getEffectsBefore().clear();
			Bukkit.getPluginManager().callEvent(new PlayerParkourFailed(playerp.getParkour(), playerp, p, reason));
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
				handlePointEffect(p, check);
				return true;
			} else {
				if (!playerp.checkAndSetLastMessage()) {
					return false;
				}
				if (playerp.getLastCheckpoint() == check.getIndex()) {
					playerp.setLastCheckpointTime(System.currentTimeMillis() - playerp.getStartTime());
					p.sendMessage(ChatColor.YELLOW + "You are now on checkpoint " + ChatColor.AQUA + "#" + (check.getDisplayIndex()));
					handlePointEffect(p, check);
				} else if ((playerp.getLastCheckpoint() + 1) < check.getIndex()) {
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
			//TODO remove effects
			return true;
		} else {
			p.sendMessage(ChatColor.RED + "You aren't in a parkour");
			return false;
		}
	}
	
	public void checkComplete(Player p, Parkour parkour) {
		if (players.containsKey(p.getUniqueId())) {
			ParkourPlayer playerp = players.get(p.getUniqueId());
			if (!parkour.equals(playerp.getParkour())) {
				return;
			}
			if (parkour.hasCheckpoints()) {
				if (playerp.getLastCheckpoint() == (parkour.getCheckpointsCount() - 1)) {
					// The player was in all the checkpoint and the latest one
					// is the last one
					callComplete(p, parkour, playerp);
				} else {
					if (!playerp.checkAndSetLastMessage()) {
						return;
					}
					p.sendMessage(ChatColor.RED + "You missed a checkpoint");
				}
			} else {
				callComplete(p, parkour, playerp);
			}

		}
	}
	
	@SuppressWarnings("deprecation")
	private void callComplete(final Player p, final Parkour parkour, final ParkourPlayer playerp) {
		final long time = System.currentTimeMillis() - playerp.getStartTime();
		if (Bukkit.isPrimaryThread()) {
			//We perform few sql queries on the completeParkour method, so better to call it async
			//The time of the player will still be correct, we calculate it before
			Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					completeParkour(p, parkour, playerp, time);
				}
			});
		} else {
			completeParkour(p, parkour, playerp, time);
		}
	}
	
	private boolean completeParkour(final Player p, final Parkour parkour, final ParkourPlayer playerp, final long time) {
		System.out.println(Thread.currentThread().getName() + " " + Bukkit.isPrimaryThread());
		players.remove(p.getUniqueId());
		ParkourPlayerScore old = plugin.getData().getBestPlayerScore(p, parkour);
		final ParkourPlayerScore now = new ParkourPlayerScore(p.getUniqueId(), parkour.getId(), playerp.getStartTime(), time);
		
		int bestrank = plugin.getData().getPlayerRank(parkour, p);
		int scoreId = plugin.getData().insertPlayerScore(now);
		int scorerank = plugin.getData().getScoreRank(parkour, scoreId);
		
		p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You completed the parkour in " + ChatColor.GREEN + Utils.formatScore(time, scorerank));
		if (old != null) {
			if (now.getTimeTook() < old.getTimeTook()) {
				p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You break your previous record of " + ChatColor.RESET + "" + ChatColor.AQUA + Utils.formatScore(old.getTimeTook(), bestrank) + ChatColor.GOLD + "" + ChatColor.BOLD + " (Improvement of " + ChatColor.GREEN + Utils.formatTime(old.getTimeTook() - now.getTimeTook()) + ChatColor.GOLD + "" + ChatColor.BOLD + ")!");
			} else {
				p.sendMessage(ChatColor.YELLOW + "Try again to beat you previous record of " + ChatColor.AQUA + Utils.formatScore(old.getTimeTook(), bestrank) + "");
			}
		}
		//p.sendMessage("old best rank: " + oldrank);
		//p.sendMessage("new best rank: " + rank);
		//p.sendMessage("score rank: " + scorerank);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new PlayerParkourComplete(parkour, playerp, p, now));
			    for (PotionEffect effect : p.getActivePotionEffects()) {
			    	p.removePotionEffect(effect.getType());
			    }
				plugin.getParkourManager().initLeaderboard(parkour);
				for (PotionEffect before : playerp.getEffectsBefore()) {
					p.addPotionEffect(before);
				}
				playerp.getEffectsBefore().clear();
			}
		});
		return true;
	}
	
	private void sendBar(Player p) {
		ParkourPlayer player = getPlayer(p);
		if (player == null) {
			return;
		}
		player.setLastActionbar(System.currentTimeMillis());
		String bar = ChatColor.GREEN + Utils.formatTime(System.currentTimeMillis() - player.getStartTime());
		plugin.getActionbar().sendActionBarRaw(p, bar);
	}
	
	private void handlePointEffect(Player p, Point point) {
		List<PointEffect> removed = new ArrayList<PointEffect>();
		ParkourPlayer player = getPlayer(p);
		for (PointEffect effect : player.getEffectsAdded()) {
			if (effect.getDuration() == -1) {
				p.removePotionEffect(effect.getType());
				removed.add(effect);
			}
		}
		player.getEffectsAdded().removeAll(removed);
		removed.clear();
		
		for (PointEffect effect : point.getEffects()) {
			PotionEffect potion = effect.createPotionEffect();
			p.removePotionEffect(potion.getType());
			p.addPotionEffect(potion);
			player.getEffectsAdded().add(effect);
		}
		
	}
	
	

}
