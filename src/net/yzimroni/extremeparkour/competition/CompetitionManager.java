package net.yzimroni.extremeparkour.competition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;

public class CompetitionManager {
	
	private ExtremeParkourPlugin plugin;
	private List<Competition> competitions = new ArrayList<Competition>();
	private Events events;
	
	public CompetitionManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		this.events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
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
