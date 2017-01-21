package net.yzimroni.extremeparkour.competition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;

public class Competition {

	private ExtremeParkourPlugin plugin;
	private CompetitionManager manager;
	private UUID leader;
	private Parkour parkour;
	private List<UUID> players = new ArrayList<UUID>();
	private CompetitionState state;
	private int taskId;
	private int timeStarting;

	public Competition(ExtremeParkourPlugin plugin, CompetitionManager manager, Player leader, Parkour parkour) {
		this.plugin = plugin;
		this.manager = manager;
		this.leader = leader.getUniqueId();
		this.parkour = parkour;
		join(leader);
	}

	public void join(Player p) {
		if (manager.isCompetes(p)) {
			return;
		}
		players.add(p.getUniqueId());
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
			leader = players.get(0);
			broadcast(getLeaderPlayer().getName() + " is now the leader");
		}

	}

	public void broadcast(String text) {
		String message = ChatColor.RED + "[" + ChatColor.GOLD + "Competition" + ChatColor.RED + "]" + ChatColor.RESET
				+ text;
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
		broadcast("Go!");
		Location l = parkour.getStartPoint().getLocation().clone().add(0.5, 2, 0.5);
		for (Player p : getBukkitPlayers()) {
			if (p.getGameMode() == GameMode.SPECTATOR) {
				p.setGameMode(GameMode.SURVIVAL);
			}
			p.setFlying(false);
			
			plugin.getParkourManager().getPlayerManager().leaveParkour(p, "");
			p.teleport(l);
			plugin.getParkourManager().getPlayerManager().startParkour(p, parkour);
		}
	}

	public boolean isCompetes(Player p) {
		return isCompetes(p.getUniqueId());
	}

	public boolean isCompetes(UUID u) {
		return players.contains(u);
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

	public List<UUID> getPlayers() {
		return players;
	}

	public List<Player> getBukkitPlayers() {
		List<Player> players = new ArrayList<Player>();
		for (UUID u : this.players) {
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

}
