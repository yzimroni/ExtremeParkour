package net.yzimroni.extremeparkour.data;

import java.util.List;

import org.bukkit.entity.Player;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;
import net.yzimroni.extremeparkour.parkour.point.Point;

public class FileData extends ExtremeParkourData {

	public FileData(ExtremeParkourPlugin plugin) {
		super(plugin);
	}

	@Override
	public void init() {

	}

	@Override
	public void save() {
		
	}

	@Override
	public void disable() {
		
	}

	@Override
	public List<Parkour> getAllParkours() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveParkour(Parkour p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertParkour(Parkour p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertPoint(Point point) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertPlayerScore(ParkourPlayerScore score) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ParkourPlayerScore getBestPlayerScore(Player p, Parkour parkour) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
