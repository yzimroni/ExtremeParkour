package net.yzimroni.extremeparkour.parkour.manager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.MaterialData;

public class ParkourManager {
	
	private ExtremeParkourPlugin plugin;
	private Events events;
	private List<Parkour> parkours = null;

	public ParkourManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		events = new Events(this);
		Bukkit.getPluginManager().registerEvents(events, plugin);
		
		parkours = plugin.getData().getAllParkours();
		for (Parkour p : parkours) {
			//TODO
			initPoint(p.getStartPoint());
			initPoint(p.getEndPoint());
			for (Checkpoint checkpoint : p.getCheckpoints()) {
				initPoint(checkpoint);
			}
		}
	}
	
	public void disable() {
		events.disable();
		for (Parkour parkour : parkours) {
			plugin.getData().saveParkour(parkour);
		}
		parkours.clear();
	}
	
	public Parkour createParkour(Player p, String name) {
		Parkour parkour = new Parkour(plugin, -1, name, p.getUniqueId(), System.currentTimeMillis());
		plugin.getData().saveParkour(parkour);
		return parkour;
	}

	public void initPoint(Point p) {
		if (p == null || p.getLocation() == null) {
			return;
		}
		
		Block block = p.getLocation().getBlock();
		Block block_below = block.getLocation().add(0, -1, 0).getBlock();
		if (block_below.getType() == null || block_below.getType() == Material.AIR || !block_below.getType().isSolid() || !block_below.getType().isBlock()) {
			block_below.setType(Material.STONE);
		}
		
		MaterialData type = p.getPointMaterial();
		block.setType(type.getMaterial());
		if (type.getData() != (byte) 0) {
			block.setData(type.getData());
		}
		
		removePointMetadata(block);
		
		block.setMetadata("parkour_id", new FixedMetadataValue(plugin, p.getParkour().getId()));
		block.setMetadata("point_type", new FixedMetadataValue(plugin, p.getClass().getName()));
		if (p instanceof Checkpoint) {
			block.setMetadata("point_index", new FixedMetadataValue(plugin, ((Checkpoint) p).getIndex()));
		}
		
		Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().getBlock().getLocation().add(0.5, 1.5, 0.5));
		for (String line : p.getHologramText()) {
			hologram.appendTextLine(line);
		}
		
		p.setHologram(hologram);
		
		//TODO hologram

	}
	
	public void removePoint(Point p) {
		if (p == null || p.getLocation() == null) {
			return;
		}
		removePointMetadata(p.getLocation().getBlock());
		//removeHologram(p);
		p.getLocation().getBlock().setType(Material.AIR);
		
		if (p.getHologram() != null) {
			p.getHologram().delete();
			p.setHologram(null);
		}
	}
	
	private void removePointMetadata(Block b) {
		removeMetadata(b, "parkour_id");
		removeMetadata(b, "point_type");
		removeMetadata(b, "point_index");
	}
	
	private void removeMetadata(Block b, String name) {
		if (b.hasMetadata(name)) {
			for (MetadataValue m : b.getMetadata(name)) {
				m.invalidate();
			}
			b.removeMetadata(name, plugin);
		}
	}
	
	public boolean isParkourBlock(Block b) {
		if (b == null) return false;
		if (b.hasMetadata("parkour_id") && !b.getMetadata("parkour_id").isEmpty()) {
			if (b.hasMetadata("point_type") && !b.getMetadata("point_type").isEmpty()) {
				String type = b.getMetadata("point_type").get(0).asString();
				if (type.equals(Checkpoint.class.getName())) {
					return b.hasMetadata("point_index") && !b.getMetadata("point_index").isEmpty();
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	public Point getPoint(Block b) {
		if (!isParkourBlock(b)) {
			return null;
		}
		int parkourId = b.getMetadata("parkour_id").get(0).asInt();
		Parkour parkour = getParkourById(parkourId);
		if (parkour == null) {
			//TODO debug
			removePointMetadata(b);
			return null;
		}
		String type = b.getMetadata("point_type").get(0).asString();
		if (type.equals(Checkpoint.class.getName())) {
			int index = b.getMetadata("point_index").get(0).asInt();
			try {
				return parkour.getCheckpoints().get(index);
			} catch (Exception e) {
				//TODO debug
				removePointMetadata(b);
				return null;
			}
		} else if (type.equals(Startpoint.class.getName())) {
			return parkour.getStartPoint();
		} else if (type.equals(Endpoint.class.getName())) {
			return parkour.getEndPoint();
		}
		return null;
	}
	
	public void fixCheckpoints(Parkour p) {
		for (int i = 0; i < p.getCheckpoints().size(); i++) {
			//TODO remove hologram
			Checkpoint point = p.getCheckpoints().get(i);
			point.setIndex(i);
			initPoint(point);
		}
	}
	

	public Parkour getParkourById(int id) {
		for (Parkour p : parkours) {
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}
	
	public List<Parkour> getParkours() {
		return parkours;
	}
			
}