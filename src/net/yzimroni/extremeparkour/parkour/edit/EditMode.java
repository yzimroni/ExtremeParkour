package net.yzimroni.extremeparkour.parkour.edit;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.Startpoint;
import net.yzimroni.extremeparkour.utils.Utils;

public class EditMode implements Listener {
	
	private ExtremeParkourPlugin plugin;
	
	private HashMap<UUID, EditData> players = new HashMap<UUID, EditData>();
	private boolean removePlayers = true;
	
	/* ITEMS */
	private ItemStack START_POINT;
	private ItemStack END_POINT;
	private ItemStack CHECK_POINT;
	
	private ItemStack SHOW_EFFECTS;
	private ItemStack ADD_EFFECT;
	private ItemStack REMOVE_EFFECT;

	public EditMode(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		initItems();
	}
	
	public void disable() {
		removePlayers = false;
		for (UUID u : players.keySet()) {
			Player p = Bukkit.getPlayer(u);
			leaveEditMode(p);
		}
		removePlayers = true;
	}
	
	private void initItems() {
		START_POINT = Utils.item(Startpoint.MATERIAL, ChatColor.AQUA + "Start point", "Place this item to set", "the parkour start point");
		END_POINT = Utils.item(Endpoint.MATERIAL, ChatColor.AQUA + "End point", "Place this item to set", "the parkour end point");
		CHECK_POINT = Utils.item(Checkpoint.MATERIAL, ChatColor.AQUA + "Check point", "Place this item to add", "a checkpoint to the parkour");
		
		SHOW_EFFECTS = Utils.item(Material.GLASS, ChatColor.GREEN + "Effects list");
		ADD_EFFECT = Utils.item(Material.BEACON, ChatColor.GREEN + "Add an effect");
		REMOVE_EFFECT = Utils.item(Material.BARRIER, ChatColor.GREEN + "Remove an effect");
	}
	
	public boolean isEditMode(Player p) {
		return players.containsKey(p.getUniqueId());
	}
	
	public EditData getData(Player p) {
		return players.get(p.getUniqueId());
	}
	
	public Parkour getParkour(Player p) {
		return getData(p).getParkour();
	}
	
	public void joinEditMode(Player p, Parkour parkour) {
		p.sendMessage("you joined edit mode");
		p.getInventory().addItem(START_POINT, CHECK_POINT, END_POINT);
		players.put(p.getUniqueId(), new EditData(p.getUniqueId(), parkour));
	}
	
	public void leaveEditMode(Player p) {
		if (isEditMode(p)) {
			if (removePlayers) {
				players.remove(p.getUniqueId());
			}
			p.getInventory().removeItem(START_POINT, CHECK_POINT, END_POINT);
			p.sendMessage("you left edit mode");
		}
	}
	
	public void toggle(Player p, Parkour parkour) {
		if (!isEditMode(p)) {
			joinEditMode(p, parkour);
		} else {
			leaveEditMode(p);
		}
	}
		
	public void onParkourDelete(Parkour parkour) {
		//TODO
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (isEditMode(e.getPlayer())) {
			Parkour parkour = getParkour(e.getPlayer());
			if (START_POINT.isSimilar(e.getItemInHand())) {
				Startpoint start = new Startpoint(-1, parkour, e.getBlockPlaced().getLocation());
				parkour.setStartPoint(start);
				parkour.initPoint(start);
			} else if (END_POINT.isSimilar(e.getItemInHand())) {
				Endpoint end = new Endpoint(-1, parkour, e.getBlockPlaced().getLocation());
				parkour.setEndPoint(end);
				parkour.initPoint(end);
			} else if (CHECK_POINT.isSimilar(e.getItemInHand())) {
				//TODO add support for shift + place
				Checkpoint check = new Checkpoint(-1, parkour, e.getBlockPlaced().getLocation(), parkour.getChestpointsCount());
				parkour.addCheckpoint(check);
			}
		}
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
				if (!p.getParkour().equals(getParkour(e.getPlayer()))) {
					//The player try to remove a point from another parkour - cancel it
					e.setCancelled(true);
					return;
				}
				if (p instanceof Checkpoint) {
					p.getParkour().removeCheckpoint((Checkpoint) p);
				} else if (p instanceof Startpoint) {
					p.getParkour().setStartPoint(null);
				} else if (p instanceof Endpoint) {
					p.getParkour().setEndPoint(null);
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (isEditMode(e.getPlayer())) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (plugin.getParkourManager().isParkourBlock(e.getClickedBlock())) {
					Point point = plugin.getParkourManager().getPoint(e.getClickedBlock());
					getData(e.getPlayer()).setPoint(point);
					openPointGUI(e.getPlayer());
					e.setCancelled(true); //Prevent interaction with the points (light detector for example)
				}
			}
		}
	}
	
	private void openPointGUI(Player p) {
		EditData data = getData(p);
		//TODO reset here what need to be reseted in data
		Inventory inv = Bukkit.createInventory(p, 9, "Point menu - " + data.getPoint().getName());
		inv.addItem(SHOW_EFFECTS, ADD_EFFECT, REMOVE_EFFECT);
		p.openInventory(inv);
	}
	
	

}
