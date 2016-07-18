package net.yzimroni.extremeparkour.parkour.manager.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class Events implements Listener {

	private ParkourPlayerManager manager;

	public Events(ParkourPlayerManager manager) {
		this.manager = manager;
	}
	
	public void disable() {
		
	}
	
	private void leaveParkour(Player p, String reason) {
		if (manager.isPakouring(p)) {
			manager.leaveParkour(p, reason);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		leaveParkour(e.getPlayer(), "");
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		leaveParkour(e.getPlayer(), "Don't teleport!");
	}
	
	@EventHandler
	public void onPlayerFly(PlayerToggleFlightEvent e) {
		leaveParkour(e.getPlayer(), "Don't fly!");
	}
	
	@EventHandler
	public void onPlayerGamemodeChange(PlayerGameModeChangeEvent e) {
		if (e.getNewGameMode() == GameMode.SPECTATOR) {
			leaveParkour(e.getPlayer(), "Don't be in specator mode!");
		}
	}
	
	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
		leaveParkour(e.getPlayer(), "Don't change worlds!");
	}
	
	@EventHandler
	public void onPlayerDie(PlayerDeathEvent e) {
		leaveParkour(e.getEntity(), "You died");
	}
	
	
}
