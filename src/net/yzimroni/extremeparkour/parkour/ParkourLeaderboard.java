package net.yzimroni.extremeparkour.parkour;

import org.bukkit.Location;

import net.yzimroni.extremeparkour.utils.DataStatus;

public class ParkourLeaderboard {

	private int id;
	private Parkour parkour;
	private Location location;
	private int playerCount;
	private int page;
	
	private DataStatus status;

	public ParkourLeaderboard(int id, Parkour parkour, Location location, int playerCount, int page) {
		super();
		this.id = id;
		this.parkour = parkour;
		this.location = location;
		this.playerCount = playerCount;
		this.page = page;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Parkour getParkour() {
		return parkour;
	}

	public void setParkour(Parkour parkour) {
		change();
		this.parkour = parkour;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		change();
		this.location = location;
	}

	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		change();
		this.playerCount = playerCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		change();
		this.page = page;
	}

	/**
	 * @return the status
	 */
	public DataStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(DataStatus status) {
		this.status = status;
	}
	
	private void change() {
		if (status == null) {
			status = DataStatus.UPDATED;
		}
	}

}
