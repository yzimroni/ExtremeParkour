package net.yzimroni.extremeparkour.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
	
	public static String formatScore(long time, int place) {
		String result = formatTime(time);
		if (place > 0) {
			result += " (Place #" + place + ")";
		}
		return result;
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
	
	public static ItemStack item(Material type, String name, String... lore) {
		ItemStack i = new ItemStack(type);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		i.setItemMeta(im);
		return i;
	}
	
	public static ItemStack item(MaterialData type, String name, String... lore) {
		ItemStack i = new ItemStack(type.getMaterial(), 1, type.getData());
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		i.setItemMeta(im);
		return i;
	}
	
	public static boolean checkPlugin(String name) {
		return Bukkit.getPluginManager().getPlugin(name) != null && Bukkit.getPluginManager().getPlugin(name).isEnabled();
	}
	
	public static List<Block> getNearbyBlocks(Location l, int radius) {
		List<Block> blocks = new ArrayList<Block>();

		for (int x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++) {
			for (int y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++) {
				for (int z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++) {
					blocks.add(l.getWorld().getBlockAt(x, y, z));
				}
			}
		}

		return blocks;
	}
	
}
