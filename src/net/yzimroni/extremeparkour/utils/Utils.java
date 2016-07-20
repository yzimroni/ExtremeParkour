package net.yzimroni.extremeparkour.utils;

import java.text.DecimalFormat;

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
	
	public static String formatTime(long time) {
		long seconds = time / 1000;
		time -= seconds * 1000;
		long minutes = seconds / 60;
		seconds -= minutes * 60;
		
		String time_s = formatLong(time);
		if (time_s.length() == 2) time_s = "0" + time_s;
		return formatLong(minutes) + ":" + formatLong(seconds) + "." + time_s;
	}
	
	private static DecimalFormat format = new DecimalFormat("#00"); 
	
	private static String formatLong(long d) {
		return format.format(d);
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.valueOf(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static int getInt(String s) {
		try {
			return Integer.valueOf(s);
		} catch (Exception e) {}
		return 0;
	}
	
}
