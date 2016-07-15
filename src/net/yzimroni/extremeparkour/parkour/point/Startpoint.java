package net.yzimroni.extremeparkour.parkour.point;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;

import net.yzimroni.extremeparkour.parkour.Parkour;

public class Startpoint extends Point {

	public Startpoint(int id, Parkour parkour, Location location) {
		super(id, parkour, location);
	}

	@Override
	public String getName() {
		return "Parkour Start";
	}

	@Override
	public List<String> getHologramText() {
		return Arrays.asList("Parkour Start");
	}

}
