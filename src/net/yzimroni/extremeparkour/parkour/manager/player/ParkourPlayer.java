package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

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

public class ParkourPlayer {

	private UUID player;

	private Parkour parkour;
	private long startTime;
	private int lastCheckpoint = -1; //So the next checkpoint is index 0
	private long lastCheckpointTime;
	
	private boolean started;
	
	private List<PotionEffect> effectsBefore = new ArrayList<PotionEffect>();
	
	private boolean teleportAllowed = false;
	
	private long lastMessage = -1;
	private long lastActionbar = -1;
	
	private List<PointEffect> effectsAdded = new ArrayList<PointEffect>();

	public ParkourPlayer(UUID player, Parkour parkour) {
		super();
		this.player = player;
		this.parkour = parkour;
		this.startTime = System.currentTimeMillis();
	}
	
	public Player getBukkitPlayer() {
		return Bukkit.getPlayer(getPlayer());
	}
		
	public void processPoint(Point point) {
		if (point instanceof Startpoint) {
			startParkour((Startpoint) point);
		} else if (point instanceof Endpoint) {
			checkComplete((Endpoint) point);
		} else if (point instanceof Checkpoint) {
			handleCheckpoint((Checkpoint) point);
		} else {
			//TODO debug
		}
	}
	
	protected boolean startParkour(Startpoint point) {
		Player p = getBukkitPlayer();
		if (p.isFlying()) {
			return false;
		}
		if (p.getGameMode() == GameMode.SPECTATOR) {
			return false;
		}
		if (!started) {
			for (PotionEffect effect : p.getActivePotionEffects()){
		    	getEffectsBefore().add(effect);
		    	p.removePotionEffect(effect.getType());
		    }
		    p.sendMessage(ChatColor.GREEN + "You start the parkour!");
			setLastMessage();
			setStartTime(System.currentTimeMillis());
			handlePointEffect(parkour.getStartPoint());
			Bukkit.getPluginManager().callEvent(new PlayerParkourStart(parkour, this, p));
			started = true;
		} else {
			if (parkour.equals(point.getParkour())) {
				//Reset the time
				if (!checkAndSetLastMessage()) {
					return false;
				}
			    for (PotionEffect effect : p.getActivePotionEffects()){
			    	p.removePotionEffect(effect.getType()); //The potion is from the parkour
			    }
			    getEffectsAdded().clear();
				setLastCheckpoint(-1);
				setLastCheckpointTime(0);
				setStartTime(System.currentTimeMillis());
				p.sendMessage(ChatColor.GREEN + "Time reset to " + Utils.formatTime(0));
				handlePointEffect(parkour.getStartPoint());

			} else {
				ExtremeParkourPlugin.get().getParkourManager().getPlayerManager().removePlayer(player);
				ExtremeParkourPlugin.get().getParkourManager().getPlayerManager().startParkour(getBukkitPlayer(), point.getParkour());
				//We are now moving the handling of this player to the new ParkourPlayer object created
				//in startParkour
				return true;
			}
		}
		return true;
	}
	
	protected boolean handleCheckpoint(Checkpoint point) {
		if (!point.getParkour().equals(parkour)) {
			return false;
		}
		Player p = getBukkitPlayer();
		//If this is the next checkpoint for the player
		if ((getLastCheckpoint() + 1) == point.getIndex()) {
			setLastCheckpoint(point.getIndex());
			setLastCheckpointTime(System.currentTimeMillis() - getStartTime());
			p.sendMessage(ChatColor.YELLOW + "You are now on checkpoint " + ChatColor.AQUA + "#" + (point.getDisplayIndex()));
			setLastMessage();
			handlePointEffect(point);
			return true;
		} else {
			if (!checkAndSetLastMessage()) {
				return false;
			}
			if (getLastCheckpoint() == point.getIndex()) {
				setLastCheckpointTime(System.currentTimeMillis() - getStartTime());
				p.sendMessage(ChatColor.YELLOW + "You are now on checkpoint " + ChatColor.AQUA + "#" + (point.getDisplayIndex()));
				handlePointEffect(point);
				return true;
			} else if ((getLastCheckpoint() + 1) < point.getIndex()) {
				p.sendMessage(ChatColor.RED + "You need to get " + point.getParkour().getCheckpoint(getLastCheckpoint() + 1).getName() + " first");
			} else {
				p.sendMessage(ChatColor.GREEN + "You already got this checkpoint"); //TODO
			}
		}
		return false;
	}
	
	public void teleportStart() {
		setTeleportAllowed(true);
		Player p = getBukkitPlayer();
		p.teleport(getParkour().getStartPoint().getLocation());
		p.sendMessage(ChatColor.GREEN + "Teleported back to " + getParkour().getStartPoint().getName());

	}
	
	public void teleportLatestCheckpoint() {
		Player p = getBukkitPlayer();

		Point latest = null;
		if (getLastCheckpoint() == -1) {
			latest = getParkour().getStartPoint();
		} else {
			latest = getParkour().getCheckpoint(getLastCheckpoint());
		}
		setTeleportAllowed(true);
		p.teleport(latest.getLocation().getBlock().getLocation().add(0.5, 1.5, 0.5));
		p.sendMessage(ChatColor.GREEN + "Teleported back to " + latest.getName());
		// TODO remove effects

	}

	
	public void sendBar() {
		setLastActionbar(System.currentTimeMillis());
		String bar = ChatColor.GREEN + Utils.formatTime(System.currentTimeMillis() - getStartTime());
		ExtremeParkourPlugin.get().getActionbar().sendActionBarRaw(getBukkitPlayer(), bar);
	}
	
	public int getNextPointIndex() {
		int next = getLastCheckpoint() + 1;
		if (next >= parkour.getCheckpointsCount()) {
			return -2; //End point
		}
		return next;
	}
	
	public Point getNextPoint() {
		return parkour.getPointByIndex(getNextPointIndex());
	}
	
	protected synchronized void checkComplete(Endpoint endpoint) {
		if (!parkour.equals(getParkour())) {
			return;
		}
		if (parkour.hasCheckpoints()) {
			if (getLastCheckpoint() == (parkour.getCheckpointsCount() - 1)) {
				// The player was in all the checkpoint and the latest one
				// is the last one
				callComplete();
			} else {
				if (!checkAndSetLastMessage()) {
					return;
				}
				getBukkitPlayer().sendMessage(ChatColor.RED + "You missed a checkpoint");
			}
		} else {
			callComplete();
		}

	}
	
	@SuppressWarnings("deprecation")
	private void callComplete() {
		final long time = System.currentTimeMillis() - getStartTime();
		if (Bukkit.isPrimaryThread()) {
			// We perform few sql queries on the completeParkour method, so
			// better to call it async
			// The time of the player will still be correct, we calculate it
			// before
			Bukkit.getScheduler().scheduleAsyncDelayedTask(ExtremeParkourPlugin.get(), new Runnable() {

				@Override
				public void run() {
					completeParkour(time);
				}
			});
		} else {
			completeParkour(time);
		}
	}
	
	private boolean completeParkour(final long time) {
		final Player p = getBukkitPlayer();
		System.out.println(Thread.currentThread().getName() + " " + Bukkit.isPrimaryThread());
		ExtremeParkourPlugin.get().getParkourManager().getPlayerManager().removePlayer(player);
		ParkourPlayerScore old = ExtremeParkourPlugin.get().getData().getBestPlayerScore(p, parkour);
		final ParkourPlayerScore now = new ParkourPlayerScore(p.getUniqueId(), parkour.getId(), getStartTime(), time);

		int bestrank = ExtremeParkourPlugin.get().getData().getPlayerRank(parkour, p);
		int scoreId = ExtremeParkourPlugin.get().getData().insertPlayerScore(now);
		int scorerank = ExtremeParkourPlugin.get().getData().getScoreRank(parkour, scoreId);

		p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You completed the parkour in " + ChatColor.GREEN + Utils.formatScore(time, scorerank));
		if (old != null) {
			if (now.getTimeTook() < old.getTimeTook()) {
				p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "You break your previous record of " + ChatColor.RESET + "" + ChatColor.AQUA + Utils.formatScore(old.getTimeTook(), bestrank) + ChatColor.GOLD + "" + ChatColor.BOLD + " (Improvement of " + ChatColor.GREEN + Utils.formatTime(old.getTimeTook() - now.getTimeTook()) + ChatColor.GOLD + "" + ChatColor.BOLD + ")!");
			} else {
				p.sendMessage(ChatColor.YELLOW + "Try again to beat you previous record of " + ChatColor.AQUA + Utils.formatScore(old.getTimeTook(), bestrank) + "");
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(ExtremeParkourPlugin.get(), new Runnable() {

			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new PlayerParkourComplete(parkour, ParkourPlayer.this, getBukkitPlayer(), now));
				for (PotionEffect effect : p.getActivePotionEffects()) {
					p.removePotionEffect(effect.getType());
				}
				ExtremeParkourPlugin.get().getParkourManager().initLeaderboard(parkour);
				for (PotionEffect before : getEffectsBefore()) {
					p.addPotionEffect(before);
				}
				getEffectsBefore().clear();
			}
		});
		return true;
	}
		
	public void leaveParkour(String reason) {
		Player p = getBukkitPlayer();
		if (reason != null && !reason.isEmpty()) {
			p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + reason);
		}
		for (PotionEffect before : getEffectsBefore()) {
			p.addPotionEffect(before);
		}
		getEffectsBefore().clear();
		Bukkit.getPluginManager().callEvent(new PlayerParkourFailed(getParkour(), this, p, reason));
		ExtremeParkourPlugin.get().getParkourManager().getPlayerManager().removePlayer(player);
	}
	
	private void handlePointEffect(Point point) {
		Player p = getBukkitPlayer();
		List<PointEffect> removed = new ArrayList<PointEffect>();
		for (PointEffect effect : getEffectsAdded()) {
			if (effect.getDuration() == -1) {
				p.removePotionEffect(effect.getType());
				removed.add(effect);
			}
		}
		getEffectsAdded().removeAll(removed);
		removed.clear();
		
		for (PointEffect effect : point.getEffects()) {
			PotionEffect potion = effect.createPotionEffect();
			p.removePotionEffect(potion.getType());
			p.addPotionEffect(potion);
			getEffectsAdded().add(effect);
		}
		
	}
	
	/* GETTERS & SETTERS */

	public UUID getPlayer() {
		return player;
	}

	public void setPlayer(UUID player) {
		this.player = player;
	}

	public Parkour getParkour() {
		return parkour;
	}

	public void setParkour(Parkour parkour) {
		this.parkour = parkour;
	}

	public int getLastCheckpoint() {
		return lastCheckpoint;
	}

	public void setLastCheckpoint(int lastCheckpoint) {
		this.lastCheckpoint = lastCheckpoint;
	}
	

	public long getLastCheckpointTime() {
		return lastCheckpointTime;
	}

	public void setLastCheckpointTime(long lastCheckpointTime) {
		this.lastCheckpointTime = lastCheckpointTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the lastMessage
	 */
	public long getLastMessage() {
		return lastMessage;
	}

	/**
	 * @param lastMessage the lastMessage to set
	 */
	public void setLastMessage(long lastMessage) {
		this.lastMessage = lastMessage;
	}
	
	public boolean checkLastMessage() {
		if (lastMessage == -1) {
			return true;
		}
		
		if (System.currentTimeMillis() - lastMessage > 1000) {
			return true;
		}
		return false;
	}
	
	public void setLastMessage() {
		lastMessage = System.currentTimeMillis();
	}
	
	public boolean checkAndSetLastMessage() {
		if (checkLastMessage()) {
			setLastMessage();
			return true;
		}
		return false;
	}

	/**
	 * @return the teleportAllowed
	 */
	public boolean isTeleportAllowed() {
		return teleportAllowed;
	}

	/**
	 * @param teleportAllowed the teleportAllowed to set
	 */
	public void setTeleportAllowed(boolean teleportAllowed) {
		this.teleportAllowed = teleportAllowed;
	}

	/**
	 * @return the lastActionbar
	 */
	public long getLastActionbar() {
		return lastActionbar;
	}

	/**
	 * @param lastActionbar the lastActionbar to set
	 */
	public void setLastActionbar(long lastActionbar) {
		this.lastActionbar = lastActionbar;
	}

	public List<PointEffect> getEffectsAdded() {
		return effectsAdded;
	}

	public void setEffectsAdded(List<PointEffect> effectsAdded) {
		this.effectsAdded = effectsAdded;
	}

	public List<PotionEffect> getEffectsBefore() {
		return effectsBefore;
	}

	public void setEffectsBefore(List<PotionEffect> effectsBefore) {
		this.effectsBefore = effectsBefore;
	}

}
