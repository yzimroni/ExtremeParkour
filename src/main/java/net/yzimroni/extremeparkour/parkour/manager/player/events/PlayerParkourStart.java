package net.yzimroni.extremeparkour.parkour.manager.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;

public class PlayerParkourStart extends PlayerParkourEvent {

	private static final HandlerList handlers = new HandlerList();

	public PlayerParkourStart(Parkour parkour, ParkourPlayer parkourPlayer, Player who) {
		super(parkour, parkourPlayer, who);
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
