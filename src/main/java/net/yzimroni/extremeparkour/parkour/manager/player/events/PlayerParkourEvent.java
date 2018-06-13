package net.yzimroni.extremeparkour.parkour.manager.player.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;

public abstract class PlayerParkourEvent extends PlayerEvent {

	private Parkour parkour;
	private ParkourPlayer parkourPlayer;

	public PlayerParkourEvent(Parkour parkour, ParkourPlayer parkourPlayer, Player who) {
		super(who);
		this.parkour = parkour;
		this.parkourPlayer = parkourPlayer;
	}

	public Parkour getParkour() {
		return parkour;
	}

	public ParkourPlayer getParkourPlayer() {
		return parkourPlayer;
	}

}
