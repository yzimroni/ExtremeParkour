package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

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
		if (this.index != index) {
			changed = true;
		}
		this.index = index;
	}

	@Override
	public String getName() {
		return "Checkpoint #" + (index + 1);
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList(getName());
	}

	@Override
	public MaterialData getPointMaterial() {
		return new MaterialData(Material.WOOD_PLATE);
	}

}
