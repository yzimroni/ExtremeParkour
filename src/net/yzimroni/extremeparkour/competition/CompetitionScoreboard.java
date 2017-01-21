package net.yzimroni.extremeparkour.competition;

import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class CompetitionScoreboard {
	
	private Competition competition;
	
	private Scoreboard scoreboard;
	private Objective objective;
	
	public CompetitionScoreboard(Competition competition) {
		this.competition = competition;
	}
	
	public void init() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Competition", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void disable() {
		for (Player p : competition.getBukkitPlayers()) {
			p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		}
	}
	
	public void changeScoreboard() {
		for (Player p : competition.getBukkitPlayers()) {
			p.setScoreboard(scoreboard);
		}
	}
	
	public void quit(Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}
	
	public void refreshPlayers() {
		for (String entry : scoreboard.getEntries()) {
			scoreboard.resetScores(entry);
		}
		
	}
	
	public LinkedHashMap<String, Integer> getSortedPlayers() {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
		//TODO
		return map;
	}

}
