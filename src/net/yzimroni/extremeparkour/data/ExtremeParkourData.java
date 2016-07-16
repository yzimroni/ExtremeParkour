package net.yzimroni.extremeparkour.data;

import java.util.List;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Point;

public abstract class ExtremeParkourData {
	
	public abstract void init();

	public abstract void save();
	
	public abstract void disable();
	
	public abstract List<Parkour> getAllParkours();
	
	public abstract void insertParkour(Parkour p);
	
	public abstract void saveParkour(Parkour p);
	
	public abstract void insertPoint(Point point);
	
}
