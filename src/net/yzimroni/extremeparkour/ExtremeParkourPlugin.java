package net.yzimroni.extremeparkour;

import org.bukkit.plugin.java.JavaPlugin;

import net.yzimroni.extremeparkour.parkour.ParkourManager;
import net.yzimroni.extremeparkour.utils.ExtremeParkourLogger;

public class ExtremeParkourPlugin extends JavaPlugin {
	
	private ParkourManager manager;

	@Override
	public void onEnable() {
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		manager = new ParkourManager(this);
		
		ExtremeParkourLogger.log("enabled");
	}
	
	@Override
	public void onDisable() {
		manager.disable();
		
		
		
		manager = null;
		ExtremeParkourLogger.log("disabled");
	}

	/**
	 * @return the parkourManager
	 */
	public ParkourManager getParkourManager() {
		return manager;
	}
	
}
