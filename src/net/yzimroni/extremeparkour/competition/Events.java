package net.yzimroni.extremeparkour.competition;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourComplete;
import net.yzimroni.extremeparkour.parkour.manager.player.events.PlayerParkourFailed;

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

	@EventHandler
	public void onPlayerParkourFailed(PlayerParkourFailed e) {
		Competition c = manager.getCompetition(e.getPlayer());
		if (c != null) {
			c.handleParkourFail(e);
		}
	}

	public void onPlayerParkourComplete(PlayerParkourComplete e) {
		Competition c = manager.getCompetition(e.getPlayer());
		if (c != null) {
			c.handleParkourComplete(e);
		}

	}

}
