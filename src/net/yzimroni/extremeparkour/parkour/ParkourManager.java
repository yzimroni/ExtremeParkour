package net.yzimroni.extremeparkour.parkour;

import java.util.List;

import org.bukkit.Bukkit;

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
	}

	public List<Parkour> getParkours() {
		return parkours;
	}
		
}
