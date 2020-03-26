package io.github.gronnmann.coinflipper.gui.configurationeditor.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.utils.coinflipper.InventoryUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;

public class BooleanChooser implements Listener{
	private BooleanChooser(){}
	private static BooleanChooser instance = new BooleanChooser();
	public static BooleanChooser getInstance(){
		return instance;
	}
	
	private Inventory selectionScreen;
	
		
	int TRUE = 3, FALSE = 5;
	
	public void setup(){
		
		
		selectionScreen = Bukkit.createInventory(new BooleanChooserHolder(), 9, "CoinFlipper ");
		
		selectionScreen.setItem(TRUE, ItemUtils.createItem(Material.WOOL, ChatColor.GREEN.toString() + ChatColor.BOLD + "TRUE", 5));
		selectionScreen.setItem(FALSE, ItemUtils.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "FALSE", 14));
		
	}
	
	
	
	public void openEditor(Player p, ConfigVar cvar){
		p.openInventory(InventoryUtils.changeName(selectionScreen, "CoinFlipper " + cvar.getPath()));
	}
	
	
	
	
	
	@EventHandler
	public void clickDetector(InventoryClickEvent e){
		if (e.getClickedInventory() == null)return;
		if (!(e.getClickedInventory().getHolder() instanceof BooleanChooserHolder))return;
		e.setCancelled(true);
		if (e.getCurrentItem().getType().equals(Material.AIR))return;
		
		if (e.getSlot() == TRUE){
			ConfigEditor.getInstance().processEditing((Player) e.getWhoClicked(), "true");
		}else if (e.getSlot() == FALSE){
			ConfigEditor.getInstance().processEditing((Player) e.getWhoClicked(), "false");
		}
		
		
		
	}
	
	@EventHandler
	public void onExit(InventoryCloseEvent e){
		if (e.getInventory() instanceof BooleanChooserHolder){
			ConfigEditor.getInstance().cvarsEdited.remove(e.getPlayer().getName());
		}
	}
	
}

class BooleanChooserHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
