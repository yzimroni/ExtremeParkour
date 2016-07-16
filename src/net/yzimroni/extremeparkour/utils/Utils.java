package net.yzimroni.extremeparkour.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {

	private Utils() {
		
	}
	
	public static String serializeLocation(Location location) {
		if (location == null) {
			return null;
		}
		return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
	}
	
	public static Location deserializeLocation(String string) {
		if (string == null || string.isEmpty()) {
			return null;
		}
		String[] parts = string.split(";");
		if (parts.length != 4) {
			//TODO debug
			return null;
		}
		World world = Bukkit.getWorld(parts[0]);
		if (world == null) {
			return null;
		}
		double x = Double.parseDouble(parts[1]);
		double y = Double.parseDouble(parts[2]);
		double z = Double.parseDouble(parts[3]);
		return new Location(world, x, y, z);
	}
	
}
