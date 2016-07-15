package net.yzimroni.extremeparkour;

import org.bukkit.plugin.java.JavaPlugin;

import net.yzimroni.extremeparkour.commands.ExtremeParkourCommands;
import net.yzimroni.extremeparkour.data.ExtremeParkourData;
import net.yzimroni.extremeparkour.data.MySqlData;
import net.yzimroni.extremeparkour.parkour.ParkourManager;
import net.yzimroni.extremeparkour.utils.ExtremeParkourLogger;

public class ExtremeParkourPlugin extends JavaPlugin {
	
	private ExtremeParkourData data;
	private ParkourManager manager;
	private ExtremeParkourCommands commands;

	@Override
	public void onEnable() {
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		initData();
		
		manager = new ParkourManager(this);
		
		commands = new ExtremeParkourCommands(this);
		
		ExtremeParkourLogger.log("enabled");
	}
	
	private void initData() {
		//TODO
		data = new MySqlData("127.0.0.1", "3306", "extremeparkour", "extremeparkour", "XGNb3vqWBQYbDyKF", "");
		data.init();
	}
	
	@Override
	public void onDisable() {
		commands.disable();
		manager.disable();
		data.disable();
		
		commands = null;
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
