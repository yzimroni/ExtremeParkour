package net.yzimroni.extremeparkour;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.yzimroni.extremeparkour.commands.ExtremeParkourCommands;
import net.yzimroni.extremeparkour.competition.CompetitionManager;
import net.yzimroni.extremeparkour.data.SQLData;
import net.yzimroni.extremeparkour.parkour.manager.ParkourManager;
import net.yzimroni.extremeparkour.utils.Actionbar;
import net.yzimroni.extremeparkour.utils.ElytraUtils;
import net.yzimroni.extremeparkour.utils.ExtremeParkourLogger;

public class ExtremeParkourPlugin extends JavaPlugin {
	
	/*
	 * TODO
	 * 
	 * V Parkour delete command
	 * V Add point effects
	 * point teleport
	 * Create GUI to edit
	 * Add parkour reward
	 * V Prevent more then one point in the same location
	 * Storage convert
	 * V Leaderboard reset
	 * V Remove all potions effects from the player before enter the parkour (and give them back afterwards)
	 */
	
	private static ExtremeParkourPlugin plugin;

	private SQLData data;
	private ParkourManager manager;
	private ExtremeParkourCommands commands;
	private Actionbar actionbar;
	private CompetitionManager competition;

	private boolean inited = false;

	@Override
	public void onEnable() {

		plugin = this;

		getConfig().options().copyDefaults(true);
		saveConfig();

		if (!initData()) {
			return;
		}

		ElytraUtils.init();
		manager = new ParkourManager(this);
		commands = new ExtremeParkourCommands(this);
		actionbar = new Actionbar(this);
		competition = new CompetitionManager(this);

		ExtremeParkourLogger.log("enabled");

		inited = true;
	}

	private boolean initData() {
		data = new SQLData(this, getConfig().getString("storage.prefix"));
		try {
			String type = getConfig().getString("storage.type").toLowerCase();
			if (type.equals("mysql")) {
				data.openMySQL(getConfig().getConfigurationSection("storage.mysql"));
			} else if (type.equals("sqlite")) {
				data.openSQLite("plugins/" + getDataFolder().getName() + "/extremeparkour.db");
			} else {
				ExtremeParkourLogger.log("Unknown data type: " + type + ", switch to sqlite");
				getConfig().set("storage.type", "sqlite");
				saveConfig();
				initData(); // Call the method again to init the sqlite
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
			// Remove all the plugin's holograms when disabled
			HologramsAPI.getHolograms(this).forEach(Hologram::delete);

			manager.disable();
			data.disable();
		}
		inited = false;
		ExtremeParkourLogger.log("disabled");
	}

	public static ExtremeParkourPlugin get() {
		return plugin;
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

	/**
	 * @return the actionbar
	 */
	public Actionbar getActionbar() {
		return actionbar;
	}

	public ExtremeParkourCommands getCommands() {
		return commands;
	}

	public CompetitionManager getCompetition() {
		return competition;
	}

}
