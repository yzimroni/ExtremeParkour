package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

public class Startpoint extends Point {

	public Startpoint(int id, Parkour parkour, Location location) {
		super(id, parkour, location);
	}

	@Override
	public String getName() {
		return "Parkour Start";
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList("Parkour Start");
	}

	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public MaterialData getPointMaterial() {
		return new MaterialData(Material.IRON_PLATE);
	}

}
