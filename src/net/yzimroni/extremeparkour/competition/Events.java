package net.yzimroni.extremeparkour.competition;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

	private CompetitionManager manager;
	
	public Events(CompetitionManager manager) {
		this.manager = manager;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Competition c = manager.getCompetition(e.getPlayer());
		if (c != null) {
			c.quit(e.getPlayer());
		}
	}
	
}
