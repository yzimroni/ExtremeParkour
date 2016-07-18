package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.UUID;

import net.yzimroni.extremeparkour.parkour.Parkour;

public class ParkourPlayer {

	private UUID player;

	private Parkour parkour;
	private long startTime;
	private int lastCheckpoint;
	private long lastCheckpointTime;

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

}
