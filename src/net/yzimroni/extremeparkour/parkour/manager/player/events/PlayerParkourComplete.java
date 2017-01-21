package net.yzimroni.extremeparkour.parkour.manager.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;

public class PlayerParkourComplete extends PlayerParkourEvent {

	private static final HandlerList handlers = new HandlerList();
	
	private ParkourPlayerScore score;

	public PlayerParkourComplete(Parkour parkour, ParkourPlayer parkourPlayer, Player who, ParkourPlayerScore score) {
		super(parkour, parkourPlayer, who);
		this.score = score;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public ParkourPlayerScore getScore() {
		return score;
	}

}
