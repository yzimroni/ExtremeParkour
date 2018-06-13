package net.yzimroni.extremeparkour.competition;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;

public class CompetitionScoreboard {

	private ExtremeParkourPlugin plugin;
	private Competition competition;

	private Scoreboard scoreboard;
	private Objective objective;
	private int taskId;

	private HashMap<UUID, String> nameCache = new HashMap<UUID, String>();

	public CompetitionScoreboard(ExtremeParkourPlugin plugin, Competition competition) {
		this.plugin = plugin;
		this.competition = competition;
	}

	public void init() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("Competition", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void disable() {
		if (taskId != 0) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = 0;
		}
		for (Player p : competition.getBukkitPlayers()) {
			// TODO change to the player's previous scoreboard?
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

	@SuppressWarnings("deprecation")
	public void startTask() {
		if (taskId != 0) {
			return;
		}
		taskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				refreshPlayers(getSortedPlayers());
			}
		}, 1L, 10L);
	}

	public void refreshPlayers(LinkedHashMap<String, Integer> list) {
		for (String entry : scoreboard.getEntries()) {
			scoreboard.resetScores(entry);
		}
		for (Entry<String, Integer> e : list.entrySet()) {
			objective.getScore(e.getKey()).setScore(e.getValue());
		}
	}

	private String getName(UUID u) {
		if (!nameCache.containsKey(u)) {
			nameCache.put(u, Bukkit.getPlayer(u).getName());
		}
		return nameCache.get(u);
	}

	public LinkedHashMap<String, Integer> getSortedPlayers() {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();

		Set<UUID> win = competition.getWinners().keySet();
		List<UUID> failed = competition.getFailed();

		List<Entry<UUID, ParkourPlayer>> on = competition.getCompetetingPlayers();
		Collections.sort(on, new Comparator<Entry<UUID, ParkourPlayer>>() {

			@Override
			public int compare(Entry<UUID, ParkourPlayer> o1, Entry<UUID, ParkourPlayer> o2) {
				int next1 = o1.getValue().getNextPointIndex();
				if (next1 == -2) { // Endpoint
					next1 = Integer.MAX_VALUE;
				}
				int next2 = o1.getValue().getNextPointIndex();
				if (next2 == -2) { // Endpoint
					next2 = Integer.MAX_VALUE;
				}
				if (next1 != next2) {
					if (next1 < next2) {
						return -1;
					} else {
						return 1;
					}
				}
				double point1 = calculateDistance(o1);
				double point2 = calculateDistance(o2);
				if (point1 < point2) {
					return -1;
				} else if (point1 > point2) {
					return 1;
				} else {
					return 0;
				}
			}

			private double calculateDistance(Entry<UUID, ParkourPlayer> e) {
				double distance = e.getValue().getNextPoint().getLocation()
						.distanceSquared(Bukkit.getPlayer(e.getKey()).getLocation());
				return distance;
			}
		});
		int place = on.size();
		for (Entry<UUID, ParkourPlayer> e : on) {
			map.put(ChatColor.GREEN + getName(e.getKey()), place--);
		}
		int sc = 1000 + win.size();
		for (UUID u : win) {
			map.put(ChatColor.GOLD + getName(u), sc--);
		}

		// TODO sort somehow the players on

		sc = -1000;
		sc += failed.size();

		for (UUID u : failed) {
			map.put(ChatColor.RED + getName(u), sc--);
		}

		return map;
	}

}
