package net.yzimroni.extremeparkour.parkour.point;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

public abstract class Point {

	private int id;
	private Parkour parkour;
	private Location location;
	protected boolean changed;

	public Point(int id, Parkour parkour, Location location) {
		super();
		this.id = id;
		this.parkour = parkour;
		this.location = location;
	}
	
	public abstract String getName();
	
	public abstract List<String> getHologramText();

	public abstract int getIndex();
	
	public abstract MaterialData getPointMaterial();
	
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
		changed = true;
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
		changed = true;
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
