package net.yzimroni.extremeparkour.parkour.point;

import org.bukkit.Location;

import net.yzimroni.extremeparkour.parkour.Parkour;

public class Checkpoint extends Point {

	private int index;

	public Checkpoint(int id, Parkour parkour, Location location, int index) {
		super(id, parkour, location);
		this.index = index;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
