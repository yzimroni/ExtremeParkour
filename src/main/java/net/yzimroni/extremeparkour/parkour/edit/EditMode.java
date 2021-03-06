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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;
import net.yzimroni.extremeparkour.parkour.Parkour;
import net.yzimroni.extremeparkour.parkour.point.Checkpoint;
import net.yzimroni.extremeparkour.parkour.point.Endpoint;
import net.yzimroni.extremeparkour.parkour.point.Point;
import net.yzimroni.extremeparkour.parkour.point.PointMode;
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
	private ItemStack SELECTED_MODE;

	private ItemStack SHOW_EFFECTS;
	private ItemStack ADD_EFFECT;
	private ItemStack REMOVE_EFFECT;

	private ItemStack BLOCK;
	private ItemStack DISTANCE;
	private ItemStack TRIPWIRE;

	public EditMode(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
		initItems();
	}

	public void disable() {
		removePlayers = false; // To prevent ConcurrentModificationException
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
		SELECTED_MODE = Utils.item(Material.BLAZE_ROD, ChatColor.LIGHT_PURPLE + "Choose point mode", "Use this item to select the point mode", "that point you place will have");
		
		SHOW_EFFECTS = Utils.item(Material.GLASS, ChatColor.GREEN + "Effects list");
		ADD_EFFECT = Utils.item(Material.BEACON, ChatColor.GREEN + "Add an effect");
		REMOVE_EFFECT = Utils.item(Material.BARRIER, ChatColor.GREEN + "Remove an effect");
		
		BLOCK = Utils.item(Material.GRASS, ChatColor.AQUA + "Block", "Players will get the point when they are standing on the point block");
		DISTANCE = Utils.item(Material.COMPASS, ChatColor.AQUA + "Distance", "Players will get the point when they are passing near the point");
		TRIPWIRE = Utils.item(Material.TRIPWIRE_HOOK, ChatColor.AQUA + "Trip wire", "Players will get the point when they are going", "Through tripwire hook near the point");
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
		p.getInventory().addItem(START_POINT, CHECK_POINT, END_POINT, SELECTED_MODE);
		players.put(p.getUniqueId(), new EditData(p.getUniqueId(), parkour));
	}

	public void leaveEditMode(Player p) {
		if (isEditMode(p)) {
			if (removePlayers) {
				players.remove(p.getUniqueId());
			}
			p.getInventory().removeItem(START_POINT, CHECK_POINT, END_POINT, SELECTED_MODE);
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
		// TODO
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (isEditMode(e.getPlayer())) {
			EditData data = getData(e.getPlayer());
			Parkour parkour = getParkour(e.getPlayer());
			if (START_POINT.isSimilar(e.getItemInHand())) {
				Startpoint start = new Startpoint(plugin, -1, parkour, e.getBlockPlaced().getLocation(),
						data.getSelectedMode(), 5);
				parkour.setStartPoint(start);
				parkour.initPoint(start);
			} else if (END_POINT.isSimilar(e.getItemInHand())) {
				Endpoint end = new Endpoint(plugin, -1, parkour, e.getBlockPlaced().getLocation(),
						data.getSelectedMode(), 5);
				parkour.setEndPoint(end);
				parkour.initPoint(end);
			} else if (CHECK_POINT.isSimilar(e.getItemInHand())) {
				// TODO add support for shift + place
				Checkpoint check = new Checkpoint(plugin, -1, parkour, e.getBlockPlaced().getLocation(),
						parkour.getCheckpointsCount(), data.getSelectedMode(), 5);
				parkour.addCheckpoint(check);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (isEditMode(e.getPlayer())) {
			Point p = null;
			if (plugin.getParkourManager().isParkourBlock(e.getBlock(), true)) {
				p = plugin.getParkourManager().getPoint(e.getBlock(), true);
			} else if (plugin.getParkourManager().isParkourBlock(e.getBlock().getLocation().add(0, 1, 0).getBlock(),
					true)) {
				p = plugin.getParkourManager().getPoint(e.getBlock().getLocation().add(0, 1, 0).getBlock(), true);
			}
			if (p != null) {
				if (!p.getParkour().equals(getParkour(e.getPlayer()))) {
					// The player try to remove a point from another parkour - cancel it
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
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (SELECTED_MODE.isSimilar(e.getItem())) {
					openPointModeGUI(e.getPlayer(), false);
					e.setCancelled(true);
					return;
				}
			}
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (plugin.getParkourManager().isParkourBlock(e.getClickedBlock(), true)) {
					Point point = plugin.getParkourManager().getPoint(e.getClickedBlock(), true);
					if (!point.getParkour().equals(getParkour(e.getPlayer()))) {
						// The player try to edit a point from another parkour - cancel it
						e.setCancelled(true);
						return;
					}
					getData(e.getPlayer()).setPoint(point);
					openPointGUI(e.getPlayer());
					e.setCancelled(true); // Prevent interaction with the points (light detector for example)
				}
			}
		}
	}

	@EventHandler
	public void onInventoryInteract(InventoryClickEvent e) {
		// TODO check if the player in our invenotry in a better way
		if (e.getInventory().getName().startsWith("Point menu - ")
				|| e.getInventory().getName().startsWith("Point Mode Menu")) {
			if (e.getWhoClicked() instanceof Player) {
				Player p = (Player) e.getWhoClicked();
				EditData data = getData(p);
				if (data == null) {
					return;
				}
				ItemStack s = e.getCurrentItem();
				if (s == null || s.getType() == null || s.getType() == Material.AIR || s.getAmount() == 0) {
					return;
				}

				if (e.getInventory().getName().startsWith("Point Mode Menu")) {
					PointMode mode = null;
					if (BLOCK.isSimilar(s)) {
						mode = PointMode.BLOCK;
					} else if (DISTANCE.isSimilar(s)) {
						mode = PointMode.DISTANCE;
					} else if (TRIPWIRE.isSimilar(s)) {
						mode = PointMode.TRIPWIRE;
					}
					if (mode == null) {
						return;
					}
					if (data.isChangeModeToPoint()) {
						data.getPoint().setMode(mode);
						data.setChangeModeToPoint(false);
						p.sendMessage(data.getPoint().getName() + "  mode changed to " + mode.name());
					} else {
						p.sendMessage("You have selected " + mode.name() + " mode");
						data.setSelectedMode(mode);
					}
					e.setCancelled(true);
					p.closeInventory();
					return;
				}

			}
		}
	}

	private void openPointGUI(Player p) {
		EditData data = getData(p);
		// TODO reset here what need to be reseted in data
		Inventory inv = Bukkit.createInventory(p, 9, "Point menu - " + data.getPoint().getName());
		inv.addItem(SHOW_EFFECTS, ADD_EFFECT, REMOVE_EFFECT);
		p.openInventory(inv);
	}

	private void openPointModeGUI(Player p, boolean toPoint) {
		EditData data = getData(p);
		data.setChangeModeToPoint(toPoint);
		Inventory inv = Bukkit.createInventory(p, 9,
				"Point Mode Menu" + (toPoint ? " - " + data.getPoint().getName() : ""));
		inv.addItem(BLOCK, DISTANCE, TRIPWIRE);
		p.openInventory(inv);
	}

}
