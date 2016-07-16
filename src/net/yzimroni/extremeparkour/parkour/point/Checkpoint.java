package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

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
	@Override
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		changed = true;
		this.index = index;
	}

	@Override
	public String getName() {
		return "Checkpoint #" + index;
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList("Checkpoint #" + index);
	}

}
