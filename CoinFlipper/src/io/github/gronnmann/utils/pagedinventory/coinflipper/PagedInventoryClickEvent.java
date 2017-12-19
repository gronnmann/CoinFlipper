package io.github.gronnmann.utils.pagedinventory.coinflipper;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PagedInventoryClickEvent extends Event{
	
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
	private ItemStack item;
	private int slot;
	private ClickType type;
	
	public PagedInventoryClickEvent(Inventory page, PagedInventory inv, Player clicker, int slot, ItemStack item, ClickType type) {
		this.pInv = inv;
		this.page = page;
		this.cl = clicker;
		this.slot = slot;
		this.item = item;
		this.type = type;
	}
	
	public PagedInventory getPagedInventory(){
		return pInv;
	}
	
	public Inventory getClickedInventory(){
		return page;
	}

	public Player getWhoClicked(){
		return cl;
	}
	public int getSlot(){
		return slot;
	}
	public ItemStack getCurrentItem(){
		return item;
	}
	
	public ClickType getClick(){
		return type;
	}
	
	
	
}
