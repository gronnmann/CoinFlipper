package io.github.gronnmann.utils.pagedinventory.coinflipper;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PagedInventoryCloseEvent extends Event{
	
	private static HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	private PagedInventory pInv;
	private Inventory page;
	private Player cl;
	
	public PagedInventoryCloseEvent(Inventory page, PagedInventory inv, Player clicker) {
		this.pInv = inv;
		this.page = page;
		this.cl = clicker;
	}
	
	public PagedInventory getPagedInventory(){
		return pInv;
	}
	
	public Inventory getInventory(){
		return page;
	}

	public Player getPlayer(){
		return cl;
	}
	
}
