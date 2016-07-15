package net.yzimroni.extremeparkour.parkour;

import org.bukkit.Bukkit;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;

public class ParkourManager {
	
	private ExtremeParkourPlugin plugin;
	private Events events;

	public ParkourManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
	}
	
	public void disable() {
		events.disable();
	}
	
}
