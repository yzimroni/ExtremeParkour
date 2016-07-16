package net.yzimroni.extremeparkour.parkour.point;

import java.util.List;

import org.bukkit.Location;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.DataStatus;

public abstract class Point {

	private int id;
	private Parkour parkour;
	private Location location;
	private boolean changed;

	public Point(int id, Parkour parkour, Location location) {
		super();
		this.id = id;
		this.parkour = parkour;
		this.location = location;
	}
	
	public abstract String getName();
	
	public abstract List<String> getHologramText();

	public abstract int getIndex();
	
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

	/**
	 * @return the changed
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * @param changed the changed to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	
	
}
