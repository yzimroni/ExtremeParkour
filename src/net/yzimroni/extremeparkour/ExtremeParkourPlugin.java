package net.yzimroni.extremeparkour;

import org.bukkit.plugin.java.JavaPlugin;

import net.yzimroni.extremeparkour.data.ExtremeParkourData;
import net.yzimroni.extremeparkour.parkour.ParkourManager;
import net.yzimroni.extremeparkour.utils.ExtremeParkourLogger;

public class ExtremeParkourPlugin extends JavaPlugin {
	
	private ExtremeParkourData data;
	private ParkourManager manager;

	@Override
	public void onEnable() {
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		initData();
		
		manager = new ParkourManager(this);
		
		ExtremeParkourLogger.log("enabled");
	}
	
	private void initData() {
		//TODO
		
		data.init();
	}
	
	@Override
	public void onDisable() {
		manager.disable();
		data.disable();
		
		
		manager = null;
		data = null;
		ExtremeParkourLogger.log("disabled");
	}

	/**
	 * @return the parkourManager
	 */
	public ParkourManager getParkourManager() {
		return manager;
	}

	/**
	 * @return the data
	 */
	public ExtremeParkourData getData() {
		return data;
	}
	
}
