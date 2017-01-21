package net.yzimroni.extremeparkour.competition;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.parkour.Parkour;

public class Competition {

	private CompetitionManager manager;
	private UUID leader;
	private Parkour parkour;
	private List<UUID> players = new ArrayList<UUID>();
	private CompetitionState state;

	public Competition(CompetitionManager manager, Player leader, Parkour parkour) {
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
		String message = ChatColor.RED + "[" + ChatColor.GOLD + "Competition" + ChatColor.RED + "]" + ChatColor.RESET + text;
		for (Player p : getBukkitPlayers()) {
			p.sendMessage(message);
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
