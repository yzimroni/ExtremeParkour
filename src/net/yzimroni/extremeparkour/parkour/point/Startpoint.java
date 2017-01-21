package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

public class Startpoint extends Point {
	
	public static MaterialData MATERIAL = new MaterialData(Material.DAYLIGHT_DETECTOR);

	public Startpoint(ExtremeParkourPlugin plugin, int id, Parkour parkour, Location location, PointMode mode, int radius) {
		super(plugin, id, parkour, location, mode, radius);
	}

	@Override
	public String getName() {
		return "Parkour Start";
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList(ChatColor.AQUA + "Parkour " + ChatColor.GREEN + "" + ChatColor.BOLD + "Start");
	}

	@Override
	public int getIndex() {
		return -1;
	}

	@Override
	public MaterialData getPointMaterial() {
		return MATERIAL;
	}

}
