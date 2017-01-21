package net.yzimroni.extremeparkour.parkour.point;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.utils.MaterialData;
import net.yzimroni.extremeparkour.utils.Utils;

public abstract class Point {
	
	private ExtremeParkourPlugin plugin;

	private int id;
	private Parkour parkour;
	private Location location;
	private List<PointEffect> effects = new ArrayList<PointEffect>();
	private PointMode mode;
	private int radius;
	
	private Hologram hologram;
	
	protected boolean changed;
	
	private List<Integer> removedEffects;

	public Point(ExtremeParkourPlugin plugin, int id, Parkour parkour, Location location, PointMode mode, int radius) {
		super();
		this.plugin = plugin;
		this.id = id;
		this.parkour = parkour;
		this.location = location;
		this.mode = mode;
		this.radius = radius;
	}
	
	public void init() {
		if (getLocation() == null) {
			return;
		}
		
		Block block = getLocation().getBlock();
		if (mode == PointMode.BLOCK) {
			Block block_below = block.getLocation().add(0, -1, 0).getBlock();
			if (block_below.getType() == null || block_below.getType() == Material.AIR || !block_below.getType().isSolid() || !block_below.getType().isBlock()) {
				block_below.setType(Material.STONE);
			}
			
			MaterialData type = getPointMaterial();
			block.setType(type.getMaterial());
			if (type.getData() != (byte) 0) {
				block.setData(type.getData());
			}
			block_below.setMetadata("extremeparkour_block", new FixedMetadataValue(plugin, true));
		}
		
		removePointMetadata(block);
		
		block.setMetadata("parkour_id", new FixedMetadataValue(plugin, getParkour().getId()));
		block.setMetadata("point_type", new FixedMetadataValue(plugin, getClass().getName()));
		if (this instanceof Checkpoint) {
			block.setMetadata("point_index", new FixedMetadataValue(plugin, ((Checkpoint) this).getIndex()));
		}
		block.setMetadata("extremeparkour_block", new FixedMetadataValue(plugin, true));
		
		Hologram hologram = HologramsAPI.createHologram(plugin, getLocation().getBlock().getLocation().add(0.5, 2, 0.5));
		for (String line : getHologramText()) {
			hologram.appendTextLine(line);
		}
		
		setHologram(hologram);
		
		if (getMode().getRadius()) {
			addRadiusBlocks();
		}

	}
	
	private void addRadiusBlocks() {
		for (Block b: getNearbyBlocks()) {
			b.setMetadata("point_radius", new FixedMetadataValue(plugin, getIndex()));
		}
	}
	
	private void removeRadiusBlocks() {
		for (Block b: getNearbyBlocks()) {
			if (b.hasMetadata("point_radius")) {
				for (MetadataValue v : b.getMetadata("point_radius")) {
					v.invalidate();
				}
				b.removeMetadata("point_radius", plugin);
			}
		}
	}
	
	private void removePointMetadata(Block b) {
		removeMetadata(b, "parkour_id");
		removeMetadata(b, "point_type");
		removeMetadata(b, "point_index");
		removeMetadata(b, "extremeparkour_block");
	}
	
	private void removeMetadata(Block b, String name) {
		if (b.hasMetadata(name)) {
			for (MetadataValue m : b.getMetadata(name)) {
				m.invalidate();
			}
			b.removeMetadata(name, plugin);
		}
	}
		
	public void remove() {
		if (getLocation() == null) {
			return;
		}
		removePointMetadata(getLocation().getBlock());
		if (getMode() == PointMode.BLOCK) {
			getLocation().getBlock().setType(Material.AIR);
		}
		
		if (getMode().getRadius()) {
			removeRadiusBlocks();
		}
		
		removeHologram();
	
	}
	
	public void removeHologram() {
		if (getHologram() != null) {
			getHologram().delete();
			setHologram(null);
		}
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
		if (mode.getRadius() != this.mode.getRadius()) {
			if (mode.getRadius()) {
				addRadiusBlocks();
			} else {
				removeRadiusBlocks();
			}
		}
		this.mode = mode;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		if (getMode().getRadius()) {
			removeRadiusBlocks();
		}
		this.radius = radius;
		if (getMode().getRadius()) {
			addRadiusBlocks();
		}
	}

	
	public List<Block> getNearbyBlocks() {
		return Utils.getNearbyBlocks(location, radius);
	}

}
