package net.yzimroni.extremeparkour.parkour;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.Utils;

public class EditMode implements Listener {
	
	private ExtremeParkourPlugin plugin;
	
	private HashMap<UUID, Parkour> players = new HashMap<UUID, Parkour>();
	
	/* ITEMS */
	private ItemStack START_POINT;
	private ItemStack END_POINT;
	private ItemStack CHECK_POINT;

	public EditMode(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		initItems();
	}
	
	private void initItems() {
		START_POINT = Utils.item(Material.BLAZE_ROD, ChatColor.AQUA + "Start point", "Place this item to set", "the parkour start point");
		END_POINT = Utils.item(Material.BLAZE_POWDER, ChatColor.AQUA + "End point", "Place this item to set", "the parkour end point");
		CHECK_POINT = Utils.item(Material.MAGMA_CREAM, ChatColor.AQUA + "Check point", "Place this item to add", "a checkpoint to the parkour");
	}
	
	public boolean isEditMode(Player p) {
		return players.containsKey(p.getUniqueId());
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (isEditMode(e.getPlayer())) {
			Point p = null;
			if (plugin.getParkourManager().isParkourBlock(e.getBlock())) {
				p = plugin.getParkourManager().getPoint(e.getBlock());
			} else if (plugin.getParkourManager().isParkourBlock(e.getBlock().getLocation().add(0, 1, 0).getBlock())) {
				p = plugin.getParkourManager().getPoint(e.getBlock().getLocation().add(0, 1, 0).getBlock());
			}
			if (p != null) {
				if (p instanceof Checkpoint) {
					p.getParkour().removeCheckpoint((Checkpoint) p);
				} else if (p instanceof Startpoint) {
					p.getParkour().setStartPoint(null);
				} else if (p instanceof Endpoint) {
					p.getParkour().setEndPoint(null);
				}
			}
		}
	}

}
