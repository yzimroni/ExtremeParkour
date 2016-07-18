package net.yzimroni.extremeparkour.data;

import java.util.List;

import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;
import net.yzimroni.extremeparkour.parkour.point.Point;

public abstract class ExtremeParkourData {
	
	protected ExtremeParkourPlugin plugin;
	
	public ExtremeParkourData(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract void init();

	public abstract void save();
	
	public abstract void disable();
	
	public abstract List<Parkour> getAllParkours();
	
	public abstract void insertParkour(Parkour p);
	
	public abstract void saveParkour(Parkour p);
	
	public abstract void insertPoint(Point point);

	public abstract void insertPlayerScore(ParkourPlayerScore score);
	
	public abstract ParkourPlayerScore getBestPlayerScore(Player p, Parkour parkour);
}
