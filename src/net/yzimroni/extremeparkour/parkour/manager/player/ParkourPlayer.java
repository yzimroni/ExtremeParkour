package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.UUID;

import net.yzimroni.extremeparkour.parkour.Parkour;

public class ParkourPlayer {

	private UUID player;

	private Parkour parkour;
	private long startTime;
	private int lastCheckpoint;
	private long lastCheckpointTime;
	
	private boolean teleportAllowed = false;
	
	private long lastMessage = -1;

	public ParkourPlayer(UUID player, Parkour parkour, long startTime) {
		super();
		this.player = player;
		this.parkour = parkour;
		this.startTime = startTime;
	}

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

}
