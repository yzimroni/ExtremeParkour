package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

public class Endpoint extends Point {
	
	public static MaterialData MATERIAL = new MaterialData(Material.GOLD_PLATE);

	public Endpoint(int id, Parkour parkour, Location location) {
		super(id, parkour, location);
	}

	@Override
	public String getName() {
		return "Parkour End";
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList(ChatColor.GOLD + "Parkour " + ChatColor.RED + "" + ChatColor.BOLD + "End");
	}

	@Override
	public int getIndex() {
		return -2;
	}

	@Override
	public MaterialData getPointMaterial() {
		return MATERIAL;
	}

}
