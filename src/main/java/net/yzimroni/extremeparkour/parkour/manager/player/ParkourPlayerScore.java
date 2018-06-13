package net.yzimroni.extremeparkour.parkour.manager.player;

import java.util.UUID;

public class ParkourPlayerScore {
	// TODO rename?

	private UUID player;
	private int parkourId;
	private long date;
	private long timeTook;

	public ParkourPlayerScore(UUID player, int parkourId, long date, long timeTook) {
		super();
		this.player = player;
		this.parkourId = parkourId;
		this.date = date;
		this.timeTook = timeTook;
	}

	public UUID getPlayer() {
		return player;
	}

	public void setPlayer(UUID player) {
		this.player = player;
	}

	public int getParkourId() {
		return parkourId;
	}

	public void setParkourId(int parkourId) {
		this.parkourId = parkourId;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getTimeTook() {
		return timeTook;
	}

	public void setTimeTook(long timeTook) {
		this.timeTook = timeTook;
	}

}
