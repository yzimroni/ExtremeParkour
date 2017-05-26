package net.yzimroni.extremeparkour.competition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;
import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourComplete;
import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourFailed;
import net.yzimroni.extremeparkour.utils.Utils;

public class Competition {

	private ExtremeParkourPlugin plugin;
	private CompetitionManager manager;
	private UUID leader;
	private Parkour parkour;
	private HashMap<UUID, ParkourPlayer> players = new HashMap<UUID, ParkourPlayer>();
	private CompetitionState state = CompetitionState.WAITING;
	private int taskId;
	private int timeStarting;
	private CompetitionScoreboard scoreboard;
	
	private LinkedHashMap<UUID, ParkourPlayerScore> winners = new LinkedHashMap<UUID, ParkourPlayerScore>();
	private List<UUID> failed = new ArrayList<UUID>();

	public Competition(ExtremeParkourPlugin plugin, CompetitionManager manager, Player leader, Parkour parkour) {
		this.plugin = plugin;
		this.manager = manager;
		this.leader = leader.getUniqueId();
		this.parkour = parkour;
		
		scoreboard = new CompetitionScoreboard(plugin, this);
		join(leader);
	}

	public void join(Player p) {
		if (manager.isCompetes(p)) {
			return;
		}
		players.put(p.getUniqueId(), null);
		p.sendMessage("You joined " + getLeaderPlayer().getName() + "'s competition");
		broadcast(p.getName() + " Joined!");
	}

	public void quit(Player p) {
		if (!isCompetes(p)) {
			return;
		}
		broadcast(p.getName() + " quit");
		players.remove(p.getUniqueId());
		

		if (players.isEmpty()) {
			manager.removeCompetition(this);
			return;
		}

		if (p.getUniqueId().equals(leader)) {
			leader = players.keySet().iterator().next();
			broadcast(getLeaderPlayer().getName() + " is now the leader");
		}

	}

	public void broadcast(String text) {
		String message = ChatColor.RED + "[" + ChatColor.GOLD + "Competition" + ChatColor.RED + "] " + ChatColor.RESET + text;
		for (Player p : getBukkitPlayers()) {
			p.sendMessage(message);
		}

	}

	public void startCountdown(Player p) {
		if (state != CompetitionState.WAITING) {
			p.sendMessage("The competition already starting!");
			return;
		}

		if (players.size() < 2) {
			p.sendMessage("Not enough players in the competition!");
			return;
		}

		state = CompetitionState.COUNTDOWN;
		timeStarting = 10;

		taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (--timeStarting == 0) {
					Bukkit.getScheduler().cancelTask(taskId);
					taskId = 0;
					start();
					return;
				}
				broadcast("Starting in " + timeStarting + " second" + (timeStarting == 1 ? "" : "s"));
			}
		}, 20L, 20L);

	}

	private void start() {
		state = CompetitionState.STARTED;
		scoreboard.init();
		broadcast("Go!");
		Location l = parkour.getStartPoint().getLocation().clone().add(0.5, 2, 0.5);
		for (Player p : getBukkitPlayers()) {
			if (p.getGameMode() == GameMode.SPECTATOR) {
				p.setGameMode(GameMode.SURVIVAL);
			}
			p.setFlying(false);

			plugin.getParkourManager().getPlayerManager().leaveParkour(p, "");
			p.teleport(l);
			ParkourPlayer par = plugin.getParkourManager().getPlayerManager().startParkour(p, parkour);
			players.remove(p.getUniqueId());
			players.put(p.getUniqueId(), par);
		}
		scoreboard.changeScoreboard();
		scoreboard.startTask();
	}
	
	public List<Entry<UUID, ParkourPlayer>> getCompetetingPlayers() {
		List<Entry<UUID, ParkourPlayer>> pl = new ArrayList<Entry<UUID, ParkourPlayer>>();
		for (Entry<UUID, ParkourPlayer> e : players.entrySet()) {
			if (e.getValue() != null) {
				pl.add(e);
			}
		}
		return pl;
	}

	protected void handleParkourFail(PlayerParkourFailed e) {
		if (players.get(e.getPlayer().getUniqueId()) != null) {
			players.remove(e.getPlayer().getUniqueId());
			players.put(e.getPlayer().getUniqueId(), null);
			failed.add(e.getPlayer().getUniqueId());
			broadcast(ChatColor.DARK_RED + e.getPlayer().getName() + " has failed the parkour");
			checkFinish();
		}
	}

	protected void handleParkourComplete(PlayerParkourComplete e) {
		if (players.get(e.getPlayer().getUniqueId()) != null) {
			players.remove(e.getPlayer().getUniqueId());
			players.put(e.getPlayer().getUniqueId(), null);
			winners.put(e.getPlayer().getUniqueId(), e.getScore());
			int place = winners.size();
			broadcast(ChatColor.GREEN + e.getPlayer().getName() + " has finish the parkour in " + Utils.formatTime(e.getScore().getTimeTook()) + " (#" + place + " place)!");
			checkFinish();
		}
	}
	
	public void checkFinish() {
		if (state == CompetitionState.STARTED) {
			if (getCompetetingPlayers().isEmpty()) {
				end();
			}
		}
	}
	
	public void end() {
		state = CompetitionState.ENDED;
		if (winners.isEmpty()) {
			broadcast("All the players have failed the parkour");
		} else {
			broadcast("All the players have finished the parkour!");
			broadcast("   " + ChatColor.GOLD + "The Winners:");
			int index = 1;
			for (ParkourPlayerScore score : winners.values()) {
				broadcast("   " + ChatColor.GOLD + (index) + ": " + Bukkit.getPlayer(score.getPlayer()).getName() + " " + Utils.formatTime(score.getTimeTook()));
				index++;
			}

		}
		scoreboard.disable();
		manager.removeCompetition(this);
	}

	public boolean isCompetes(Player p) {
		return isCompetes(p.getUniqueId());
	}

	public boolean isCompetes(UUID u) {
		return players.containsKey(u);
	}

	public UUID getLeader() {
		return leader;
	}

	public Player getLeaderPlayer() {
		return Bukkit.getPlayer(getLeader());
	}

	public Parkour getParkour() {
		return parkour;
	}

	public Collection<ParkourPlayer> getPlayers() {
		return players.values();
	}

	public List<Player> getBukkitPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (UUID u : this.players.keySet()) {
			players.add(Bukkit.getPlayer(u));
		}
		return players;
	}

	public CompetitionState getState() {
		return state;
	}

	public void setState(CompetitionState state) {
		this.state = state;
	}

	public CompetitionScoreboard getScoreboard() {
		return scoreboard;
	}

	public LinkedHashMap<UUID, ParkourPlayerScore> getWinners() {
		return winners;
	}

	public List<UUID> getFailed() {
		return failed;
	}

}
