package net.yzimroni.extremeparkour.parkour;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;

public class ParkourManager {
	
	private ExtremeParkourPlugin plugin;
	private Events events;
	private List<Parkour> parkours = null;

	public ParkourManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
		
		parkours = plugin.getData().getAllParkours();
	}
	
	public void disable() {
		events.disable();
		for (Parkour parkour : parkours) {
			if (parkour.hasChanged()) {
				plugin.getData().saveParkour(parkour);
			}
		}
		parkours.clear();
	}

	public List<Parkour> getParkours() {
		return parkours;
	}
	
	public Parkour createParkour(Player p, String name) {
		Parkour parkour = new Parkour(-1, name, p.getUniqueId(), System.currentTimeMillis());
		plugin.getData().saveParkour(parkour);
		return parkour;
	}
		
}
