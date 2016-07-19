package net.yzimroni.extremeparkour;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.yzimroni.extremeparkour.commands.ExtremeParkourCommands;
import net.yzimroni.extremeparkour.data.SQLData;
import net.yzimroni.extremeparkour.parkour.manager.ParkourManager;
import net.yzimroni.extremeparkour.utils.ExtremeParkourLogger;

public class ExtremeParkourPlugin extends JavaPlugin {
	
	/*
	 * TODO
	 * V Save points
	 * V Command to set start & end
	 * V Command to add a checkpoint and remove
	 * V Allow players to start & end the parkour
	 * V Add point hologram
	 * V Command to insert a checkpoint
	 * V Checkpoint command
	 * Add actionbar time count system (with protocol lib)
	 * 
	 * Create GUI to edit
	 * 
	 * Add point effects
	 * V Add ladderboard
	 * Add parkour reward
	 * X (added sqlite instead)Add FileData
	 * V Parkour protection (fly, teleport etc)
	 * 
	 * Prevent more then one point in the same location
	 * V Checkpoint index fixing
	 */
	
	private SQLData data;
	private ParkourManager manager;
	private ExtremeParkourCommands commands;
	
	private boolean inited = false;

	@Override
	public void onEnable() {
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if (!initData()) {
			return;
		}
		
		manager = new ParkourManager(this);
		
		commands = new ExtremeParkourCommands(this);
		
		ExtremeParkourLogger.log("enabled");
		
		inited = true;
	}
	
	private boolean initData() {
		data = new SQLData(this, getConfig().getString("storage.prefix"));
		try {
			String type = getConfig().getString("storage.type").toLowerCase();
			if (type.equals("mysql")) {
				data.openMySQL(getConfig().getString("storage.mysql.host"), getConfig().getString("storage.mysql.port"),
						getConfig().getString("storage.mysql.database"), getConfig().getString("storage.mysql.username"),
						getConfig().getString("storage.mysql.password"));
			} else if (type.equals("sqlite")) {
				data.openSQLite("plugins/" + getDataFolder().getName() + "/extremeparkour.db");
			} else {
				ExtremeParkourLogger.log("Unknown data type: " + type + ", switch to sqlite");
				getConfig().set("storage.type", "sqlite");
				saveConfig();
				initData(); //Call the method again to init the sqlite
			}
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return false;
		}
		return true;
	}
	
	@Override
	public void onDisable() {
		
		if (inited) {
			//Remove all the plugin's holograms when disabled
			Collection<Hologram> hs = HologramsAPI.getHolograms(this);
			if (!hs.isEmpty()) {
				for (Hologram h : hs) {
					h.delete();
				}
			}
			
			commands.disable();
			manager.disable();
			data.disable();
			
			commands = null;
			manager = null;
			data = null;
		}
		inited = false;
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
	public SQLData getData() {
		return data;
	}
	
}
