package net.yzimroni.extremeparkour.parkour.point;

import org.bukkit.Location;

import net.yzimroni.extremeparkour.parkour.Parkour;

public abstract class Point {

	private int id;
	private Parkour parkour;
	private Location location;

	public Point(int id, Parkour parkour, Location location) {
		super();
		this.id = id;
		this.parkour = parkour;
		this.location = location;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the parkour
	 */
	public Parkour getParkour() {
		return parkour;
	}

	/**
	 * @param parkour
	 *            the parkour to set
	 */
	public void setParkour(Parkour parkour) {
		this.parkour = parkour;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

}
