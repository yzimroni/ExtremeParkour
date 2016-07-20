package net.yzimroni.extremeparkour.parkour.point;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.yzimroni.extremeparkour.utils.DataStatus;

public class PointEffect {

	private int id;
	private PotionEffectType type;
	private int duration; // -1 to stay until the next point
	private int amplifier;
	private boolean showParticles;

	private DataStatus status;

	public PointEffect(int id, PotionEffectType type, int duration, int amplifier, boolean showParticles) {
		this.id = id;
		this.type = type;
		this.duration = duration;
		this.amplifier = amplifier;
		this.setShowParticles(showParticles);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PotionEffectType getType() {
		return type;
	}

	public void setType(PotionEffectType type) {
		this.type = type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public void setAmplifier(int amplifier) {
		this.amplifier = amplifier;
	}

	public boolean isShowParticles() {
		return showParticles;
	}

	public void setShowParticles(boolean showParticles) {
		this.showParticles = showParticles;
	}

	public DataStatus getStatus() {
		return status;
	}

	public void setStatus(DataStatus status) {
		this.status = status;
	}
	
	public PotionEffect createPotionEffect() {
		PotionEffect effect = new PotionEffect(type, duration == -1 ? Integer.MAX_VALUE : duration, amplifier);
		return effect;
	}

}
