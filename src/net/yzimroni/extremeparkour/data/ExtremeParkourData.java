package net.yzimroni.extremeparkour.data;

import java.util.List;

import net.yzimroni.extremeparkour.parkour.Parkour;

public abstract class ExtremeParkourData {
	
	public abstract void init();

	public abstract void save();
	
	public abstract void disable();
	
	public abstract List<Parkour> getAllParkours();
	
	public abstract void saveParkour(Parkour p);
	
}
