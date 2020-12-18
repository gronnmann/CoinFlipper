package io.github.gronnmann.utils.coinflipper.input;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.utils.coinflipper.InventoryUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.coinflipper.input.InputData.InputType;

public class BooleanChooser implements Listener{
	private BooleanChooser(){}
	private static BooleanChooser instance = new BooleanChooser();
	public static BooleanChooser getInstance(){
		return instance;
	}
	
	private Inventory selectionScreen;
	
		
	int TRUE = 3, FALSE = 5, BACK = 0;;
	
	public void setup(){
		
		
		selectionScreen = Bukkit.createInventory(new BooleanChooserHolder(), 9, "CoinFlipper ");
		
		selectionScreen.setItem(BACK, ItemUtils.createItem(Material.INK_SACK, Message.BACK.getMessage(), 1));
		selectionScreen.setItem(TRUE, ItemUtils.createItem(Material.WOOL, ChatColor.GREEN.toString() + ChatColor.BOLD + "TRUE", 5));
		selectionScreen.setItem(FALSE, ItemUtils.createItem(Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "FALSE", 14));
		
	}
	
	
	
	public void openEditor(Player p, InputData params){
		p.openInventory(InventoryUtils.changeName(selectionScreen, "CoinFlipper " + params.getExtraData("CVAR")));
	}
	
	
	
	
	
	@EventHandler
	public void clickDetector(InventoryClickEvent e){
		if (e.getClickedInventory() == null)return;
		if (!(e.getClickedInventory().getHolder() instanceof BooleanChooserHolder))return;
		e.setCancelled(true);
		if (e.getCurrentItem().getType().equals(Material.AIR))return;
		
		if (e.getSlot() == BACK) {
			InputData params = InputManager.getData(e.getWhoClicked().getName());
			if (params != null && params.getType() == InputType.BOOLEAN) {
				e.getWhoClicked().openInventory((Inventory) params.getExtraData("RETURN_INVENTORY"));
			}else {
				e.getWhoClicked().closeInventory();
			}
		}else if (e.getSlot() == TRUE){
			InputManager.processInput(e.getWhoClicked().getName(), "true");
		}else if (e.getSlot() == FALSE){
			InputManager.processInput(e.getWhoClicked().getName(), "false");
		}
		
		
		
	}
	
	@EventHandler
	public void stopInventoryLeak(InventoryCloseEvent e) {
		if (!(e.getInventory().getHolder() instanceof BooleanChooserHolder))return;
		if (InputManager.getData(e.getPlayer().getName()) != null && InputManager.getData(e.getPlayer().getName()).getType() == InputType.BOOLEAN) {
			InputManager.removeInput(e.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void cancelDrag(InventoryDragEvent e) {
		if (e.getInventory().getHolder() instanceof BooleanChooserHolder)e.setCancelled(true);
	}
	
}

class BooleanChooserHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
