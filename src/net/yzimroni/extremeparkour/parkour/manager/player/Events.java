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

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;

public class Events implements Listener {

	private ExtremeParkourPlugin plugin;
	private ParkourPlayerManager manager;

	public Events(ExtremeParkourPlugin plugin, ParkourPlayerManager manager) {
		this.plugin = plugin;
		this.manager = manager;
	}
	
	public void disable() {
		
	}
	
	private void leaveParkour(Player p, String reason) {
		if (manager.isPakouring(p)) {
			manager.getPlayer(p).leaveParkour(reason);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		leaveParkour(e.getPlayer(), "");
		plugin.getParkourManager().getEditMode().leaveEditMode(e.getPlayer());
		plugin.getCommands().onPlayerQuit(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		ParkourPlayer playerp = manager.getPlayer(e.getPlayer());
		if (playerp != null) {
			if (playerp.isTeleportAllowed()) {
				playerp.setTeleportAllowed(false);
			} else {
				leaveParkour(e.getPlayer(), "Don't teleport!");
			}
		}
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
