package io.github.gronnmann.utils.pagedinventory.coinflipper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;


public class PagedInventoryManager implements Listener{
	@EventHandler
	public void pageBrowsing(InventoryClickEvent e){
		if (e.getClickedInventory() == null)return;
		if (!(e.getClickedInventory().getHolder() instanceof PagedInventoryHolder))return;
		e.setCancelled(true);
		
		Player p = (Player)e.getWhoClicked();
		
		PagedInventory inv = PagedInventory.getByInventory(e.getClickedInventory());
		
		if (inv == null)return;
		
		
		if (e.getSlot() == PagedInventory.NEXT){
			int next = inv.getNumber(e.getClickedInventory())+1;
			if (next > inv.sizePages()-1){
				next = inv.sizePages()-1;
			}
			
			p.openInventory(inv.getPage(next));
		}else if (e.getSlot() == PagedInventory.PREV){
			int prev = inv.getNumber(e.getClickedInventory())-1;
			if (prev < 0){
				prev = 0;
			}
			
			p.openInventory(inv.getPage(prev));
		}else if (e.getSlot() == PagedInventory.BACK){
			e.getWhoClicked().openInventory(inv.redirectToBack);
			Bukkit.getPluginManager().callEvent(new PagedInventoryCloseEvent(e.getClickedInventory(), inv, p));
		}else{
			Bukkit.getPluginManager().callEvent(new PagedInventoryClickEvent(e.getClickedInventory(), inv, p, e.getSlot(), e.getCurrentItem(), e.getClick()));
			
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e){
		if (!(e.getInventory().getHolder() instanceof PagedInventory))return;
		Bukkit.getPluginManager().callEvent(new PagedInventoryCloseEvent(e.getInventory(), PagedInventory.getByInventory(e.getInventory()), (Player) e.getPlayer()));
	}
}
