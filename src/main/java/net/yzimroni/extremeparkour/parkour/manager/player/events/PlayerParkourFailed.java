package net.yzimroni.extremeparkour.parkour.manager.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;

public class PlayerParkourFailed extends PlayerParkourEvent {

	private static final HandlerList handlers = new HandlerList();

	private String reason;

	public PlayerParkourFailed(Parkour parkour, ParkourPlayer parkourPlayer, Player who, String reason) {
		super(parkour, parkourPlayer, who);
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
