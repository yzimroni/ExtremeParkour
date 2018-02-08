package net.yzimroni.extremeparkour.competition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;

public class CompetitionManager {

	private ExtremeParkourPlugin plugin;
	private List<Competition> competitions = new ArrayList<Competition>();
	private Events events;
	private CompetitionCommands commands;

	public CompetitionManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		this.events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
		commands = new CompetitionCommands(plugin, this);
	}

	public Competition createCompetition(Player p) {
		if (isCompetes(p)) {
			return null;
		}
		ParkourPlayer playerp = plugin.getParkourManager().getPlayerManager().getPlayer(p);
		Parkour parkour;
		if (playerp == null || (parkour = playerp.getParkour()) == null) {
			p.sendMessage("You must start a parkour if you want to create a competition");
			return null;
		}

		playerp.leaveParkour(""); // TODO
		Competition c = new Competition(plugin, this, p, parkour);
		competitions.add(c);
		return c;
	}

	public Competition getCompetition(Player p) {
		for (Competition c : competitions) {
			if (c.isCompetes(p)) {
				return c;
			}
		}
		return null;
	}

	public boolean isCompetes(UUID u) {
		for (Competition c : competitions) {
			if (c.isCompetes(u)) {
				return true;
			}
		}
		return false;
	}

	public boolean isCompetes(Player p) {
		return isCompetes(p.getUniqueId());
	}

	public List<Competition> getCompetitions() {
		return competitions;
	}

	public void removeCompetition(Competition c) {
		competitions.remove(c);
	}

}
