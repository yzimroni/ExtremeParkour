package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

public class Checkpoint extends Point {
	
	public static MaterialData MATERIAL = new MaterialData(Material.IRON_PLATE);

	private int index;

	public Checkpoint(int id, Parkour parkour, Location location, int index, PointMode mode, double distance) {
		super(id, parkour, location, mode, distance);
		this.index = index;
	}

	/**
	 * @return the index
	 */
	@Override
	public int getIndex() {
		return index;
	}
	
	public int getDisplayIndex() {
		return index + 1;
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
		return "Checkpoint #" + getDisplayIndex();
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList(ChatColor.AQUA + "" + ChatColor.BOLD + "Checkpoint " + ChatColor.YELLOW + "#" + getDisplayIndex());
	}

	@Override
	public MaterialData getPointMaterial() {
		return MATERIAL;
	}

}
