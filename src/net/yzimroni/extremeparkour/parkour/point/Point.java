package net.yzimroni.extremeparkour.parkour.point;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import com.gmail.filoghost.holographicdisplays.api.Hologram;

import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;

public abstract class Point {

	private int id;
	private Parkour parkour;
	private Location location;
	private List<PointEffect> effects = new ArrayList<PointEffect>();
	private PointMode mode;
	private double distance;
	
	private Hologram hologram;
	
	protected boolean changed;
	
	private List<Integer> removedEffects;

	public Point(int id, Parkour parkour, Location location, PointMode mode, double distance) {
		super();
		this.id = id;
		this.parkour = parkour;
		this.location = location;
		this.mode = mode;
		this.distance = distance;
	}
	
	public abstract String getName();
	
	public abstract List<String> getHologramText();

	public abstract int getIndex();
	
	public abstract MaterialData getPointMaterial();
	
	public boolean hasEffect(PotionEffectType type) {
		for (PointEffect effect : effects) {
			if (effect.getType().equals(type)) {
				return true;
			}
		}
		return false;
	}
	
	public void removeEffect(PointEffect effect) {
		if (effects.contains(effect)) {
			effects.remove(effect);
			if (removedEffects == null) {
				removedEffects = new ArrayList<Integer>();
			}
			removedEffects.add(effect.getId());
		}
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the parkour
	 */
	public Parkour getParkour() {
		return parkour;
	}

	/**
	 * @param parkour
	 *            the parkour to set
	 */
	public void setParkour(Parkour parkour) {
		changed = true;
		this.parkour = parkour;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		changed = true;
		this.location = location;
	}

	/**
	 * @return the changed
	 */
	public boolean hasChanged() {
		return changed;
	}

	/**
	 * @param changed the changed to set
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	/**
	 * @return the hologram
	 */
	public Hologram getHologram() {
		return hologram;
	}

	/**
	 * @param hologram the hologram to set
	 */
	public void setHologram(Hologram hologram) {
		this.hologram = hologram;
	}

	public List<PointEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<PointEffect> effects) {
		this.effects = effects;
	}

	public List<Integer> getRemovedEffects() {
		return removedEffects;
	}

	public void setRemovedEffects(List<Integer> removedEffects) {
		this.removedEffects = removedEffects;
	}

	public PointMode getMode() {
		return mode;
	}

	public void setMode(PointMode mode) {
		changed = true;
		this.mode = mode;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		changed = true;
		this.distance = distance;
	}

}
