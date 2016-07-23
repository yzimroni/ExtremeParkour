package net.yzimroni.extremeparkour.parkour.manager;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;

import net.yzimroni.extremeparkour.ExtremeParkourPlugin;

public class Events implements Listener {

	private ExtremeParkourPlugin plugin;

	public Events(ExtremeParkourPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void disable() {
		
	}
	
	private boolean isEditMode(Player p) {
		return plugin.getParkourManager().getEditMode().isEditMode(p);
	}
	
	private boolean isPointOrPointSupport(Block b) {
		if (plugin.getParkourManager().isParkourBlock(b)) {
			return true;
		} else if (plugin.getParkourManager().isParkourBlock(b.getLocation().add(0, 1, 0).getBlock())) {
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (!isEditMode(e.getPlayer())) {
			if (isPointOrPointSupport(e.getBlock())) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent e) {
		if (isPointOrPointSupport(e.getBlock())) {
			e.setCancelled(true);
		}
	}

}
