package net.yzimroni.extremeparkour.parkour.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.ParkourLeaderboard;
import net.yzimroni.extremeparkour.parkour.edit.EditMode;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayer;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerManager;
import net.yzimroni.extremeparkour.parkour.manager.player.ParkourPlayerScore;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.MaterialData;
import net.yzimroni.extremeparkour.utils.Utils;

public class ParkourManager {
	
	private ExtremeParkourPlugin plugin;
	private Events events;
	private ParkourPlayerManager playerManager;
	private EditMode editMode;
	private List<Parkour> parkours = null;
	private Comparator<Checkpoint> checkpointComparator;

	public ParkourManager(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		events = new Events(plugin);
		Bukkit.getPluginManager().registerEvents(events, plugin);
		playerManager = new ParkourPlayerManager(plugin);
		Bukkit.getPluginManager().registerEvents(playerManager, plugin);

		checkpointComparator = new Comparator<Checkpoint>() {
			
			@Override
			public int compare(Checkpoint o1, Checkpoint o2) {
				return Integer.compare(o1.getIndex(), o2.getIndex());
			}
		};
		
		parkours = plugin.getData().getAllParkours();
		for (Parkour p : parkours) {
			Collections.sort(p.getCheckpoints(), checkpointComparator);
			//TODO
			initPoint(p.getStartPoint());
			initPoint(p.getEndPoint());
			for (Checkpoint checkpoint : p.getCheckpoints()) {
				initPoint(checkpoint);
			}
			initLeaderboard(p);
		}
		editMode = new EditMode(plugin);
		Bukkit.getPluginManager().registerEvents(editMode, plugin);
	}
	
	public void disable() {
		editMode.disable();
		events.disable();
		playerManager.disable();
		for (Parkour parkour : parkours) {
			plugin.getData().saveParkour(parkour);
			removePointMetadata(parkour.getStartPoint());
			removePointMetadata(parkour.getEndPoint());
			for (Checkpoint checkpoint : parkour.getCheckpoints()) {
				removePointMetadata(checkpoint);
			}
		}
		parkours.clear();
	}
	
	public Parkour createParkour(Player p, String name) {
		Parkour parkour = new Parkour(plugin, -1, name, p.getUniqueId(), System.currentTimeMillis());
		plugin.getData().insertParkour(parkour);
		parkours.add(parkour);
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
		
		Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().getBlock().getLocation().add(0.5, 2, 0.5));
		for (String line : p.getHologramText()) {
			hologram.appendTextLine(line);
		}
		
		p.setHologram(hologram);

	}
	
	public void initLeaderboard(Parkour parkour) {
		if (!parkour.getLeaderboards().isEmpty()) {
			int max = -1;
			for (ParkourLeaderboard leaderboard : parkour.getLeaderboards()) {
				if (leaderboard.getPlayerCount() > max) {
					max = leaderboard.getPlayerCount();
				}
			}
			if (max > 150) {
				max = 150;
			}

			List<ParkourPlayerScore> top = plugin.getData().getTopPlayerScore(parkour, max);
			HashMap<UUID, String> nameCache = new HashMap<UUID, String>();
			for (ParkourLeaderboard leaderboard : parkour.getLeaderboards()) {
				removeLeaderboard(leaderboard);
				Hologram hologram = HologramsAPI.createHologram(plugin, leaderboard.getLocation());
				ItemStack diamond = new ItemStack(Material.DIAMOND);
				diamond.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				hologram.appendItemLine(diamond);
				for (int i = 0; i < Math.min(leaderboard.getPlayerCount(), top.size()); i++) {
					ParkourPlayerScore score = top.get(i);
					String name = "";
					if (nameCache.containsKey(score.getPlayer())) {
						name = nameCache.get(score.getPlayer());
					} else {
						OfflinePlayer offline = Bukkit.getOfflinePlayer(score.getPlayer());
						nameCache.put(score.getPlayer(), offline.getName());
						name = offline.getName();
					}
					hologram.appendTextLine(ChatColor.GRAY + "" + ChatColor.BOLD + (i + 1) + ". " + ChatColor.GREEN + name + ChatColor.WHITE + " @ " + ChatColor.GOLD + Utils.formatTime(score.getTimeTook()));
				}
				hologram.teleport(hologram.getLocation().add(0, hologram.getHeight(), 0));
				leaderboard.setHologram(hologram);
			}
		}
	}
	
	public void removePoint(Point p) {
		if (p == null || p.getLocation() == null) {
			return;
		}
		removePointMetadata(p.getLocation().getBlock());
		p.getLocation().getBlock().setType(Material.AIR);
		
		removeHologram(p);
	}
	
	public void removeLeaderboard(ParkourLeaderboard leaderboard) {
		if (leaderboard.getHologram() != null) {
			leaderboard.getHologram().delete();
			leaderboard.setHologram(null);
		}
	}
	
	public void removeHologram(Point p) {
		if (p.getHologram() != null) {
			p.getHologram().delete();
			p.setHologram(null);
		}
	}
	
	private void removePointMetadata(Point p) {
		if (p != null && p.getLocation() != null) {
			removePointMetadata(p.getLocation().getBlock());
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
			Checkpoint point = p.getCheckpoints().get(i);
			removeHologram(point);
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
	
	public void removeParkour(Parkour p) {
		if (parkours.contains(p)) {
			for (ParkourPlayer player : playerManager.getParkourPlayers(p)) {
				playerManager.leaveParkour(player.getBukkitPlayer(), "Parkour deleted");
			}
			parkours.remove(p);
			
			editMode.onParkourDelete(p);
			
			for (ParkourLeaderboard leaderboard : p.getLeaderboards()) {
				removeLeaderboard(leaderboard);
			}
			
			removePoint(p.getStartPoint());
			removePoint(p.getEndPoint());
			for (Checkpoint checkpoint : p.getCheckpoints()) {
				removePoint(checkpoint);
			}
			plugin.getData().deleteParkour(p);
		}
	}
	
	public List<Parkour> getParkours() {
		return parkours;
	}

	public ParkourPlayerManager getPlayerManager() {
		return playerManager;
	}

	public EditMode getEditMode() {
		return editMode;
	}
			
}
