package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Event listener for the treasure manager.
 * 
 * @author Mitch
 *
 */
class TreasureChestListener implements Listener {

	private TreasureManager control;
	
	/**
	 * Constructor
	 * @param manager the treasure manager
	 */
	TreasureChestListener(TreasureManager manager) {
		this.control = manager;
	}
	
	// TODO StackOverflowError workaround start
	@EventHandler(priority=EventPriority.NORMAL)
	public void shiftClickWorkaround(InventoryClickEvent event) {
		if(!event.isShiftClick()) {
			return;
		}
		if(event.getInventory().getType().equals(InventoryType.CHEST)) {
			return;
		}
		InventoryHolder holder = event.getInventory().getHolder();
		if(holder instanceof BlockState || holder instanceof DoubleChest) {
			Location loc = TreasureManager.getLocation(event.getInventory().getHolder());
			if(control.hasTreasure(loc)) {
				event.setCancelled(true);
			}
		}
		
	}
	// TODO StackOverflowError workaround end
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		control.onPlayerInteract(event);
	}
	
	
	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockBreak(org.bukkit.event.block.BlockBreakEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(final BlockBreakEvent event) {
		Block block = event.getBlock();
		if(block != null && block.getState() instanceof InventoryHolder) {
			InventoryHolder holder = (InventoryHolder)block.getState();
			
			Location loc = TreasureManager.getLocation(holder);
			if(control.hasTreasure(loc)) {
				event.setCancelled(true);
			}
			
		}
		
	}


	/* (non-Javadoc)
	 * @see org.bukkit.event.block.BlockListener#onBlockIgnite(org.bukkit.event.block.BlockIgniteEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(final BlockIgniteEvent event) {
		// fire protection
		Block block = event.getBlock();
		if(block != null && block.getState() instanceof InventoryHolder) {
			InventoryHolder holder = ((InventoryHolder)block.getState()).getInventory().getHolder();

			Location loc = TreasureManager.getLocation(holder);
			if(control.hasTreasure(loc)) {
				event.setCancelled(true);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.bukkit.event.entity.EntityListener#onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent)
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityExplode(final EntityExplodeEvent event) {
		Iterator<Block> blockList = event.blockList().iterator();
		boolean cancelled = false;
		while (blockList.hasNext()) {
			Block block = blockList.next();
			if(!(block.getState() instanceof InventoryHolder)) {
				continue;
			}
			InventoryHolder holder = ((InventoryHolder)block.getState()).getInventory().getHolder();
			
			Location loc = TreasureManager.getLocation(holder);
			
			if(control.hasTreasure(loc)) {
				cancelled = true;
				break;
			}
		}
		if(cancelled) {
			event.blockList().clear();
		}
	}
	
	
	private boolean isInventoryEmpty(Inventory inventory) {
		int n = inventory.getSize();
		for (int i = 0; i < n; i++) {
			ItemStack item = inventory.getItem(i);
			if(item != null && item.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	
	
}
