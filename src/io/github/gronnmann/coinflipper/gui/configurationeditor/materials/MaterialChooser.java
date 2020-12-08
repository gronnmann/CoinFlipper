package io.github.gronnmann.coinflipper.gui.configurationeditor.materials;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.pagedinventory.coinflipper.PagedInventory;
import io.github.gronnmann.utils.pagedinventory.coinflipper.PagedInventoryClickEvent;
import io.github.gronnmann.utils.pagedinventory.coinflipper.PagedInventoryCloseEvent;

public class MaterialChooser implements Listener{
	private MaterialChooser(){}
	private static MaterialChooser instance = new MaterialChooser();
	public static MaterialChooser getInstance(){
		return instance;
	}
	
	private PagedInventory selectionScreen;
	
		
	int TRUE = 3, FALSE = 5;
	
	public void setup(){
		
		selectionScreen = new PagedInventory("Select material", ItemUtils.createItem(Material.ARROW, Message.NEXT.getMessage()),
				ItemUtils.createItem(Material.ARROW, Message.PREVIOUS.getMessage()),
				ItemUtils.createItem(Material.INK_SACK, Message.BACK.getMessage(), 1),
				"material_choose", MaterialEditor.getInstance().selectionScreen);
		
		for (Material material : Material.values()){
			try{
				
				for (int i = 1; i <= ItemUtils.getDataAmount(material); i++){
					
					
					ItemStack item = ItemUtils.createItem(material, ChatColor.GOLD.toString() + material.toString(), i-1);
					
					ItemUtils.addToLore(item, ChatColor.YELLOW + "Data: " + ChatColor.GREEN + item.getDurability());
					
					ItemUtils.addToLore(item, ChatColor.YELLOW + "Left-click to set Material.");
					selectionScreen.addItem(item);
				}
				
			}catch(Exception e){continue;}
		}
		
	}
	
	
	public void openEditor(Player p, String cvar){
		p.openInventory(selectionScreen.getPage(0));
	}
	
	@EventHandler
	public void selectNewMaterial(PagedInventoryClickEvent e){
		if (!(e.getPagedInventory().getId().equals("material_choose")))return;
		if (e.getSlot() >= PagedInventory.usableSlots)return;
		
		
		ItemStack item = e.getCurrentItem();
		
		
		
		if (e.getClick().equals(ClickType.LEFT)){
			MaterialEditor.getInstance().processEditing(e.getWhoClicked(), item);
		}else return;
		
		
	}
	
	@EventHandler
	public void removeOnClose(PagedInventoryCloseEvent e){
		if (!(e.getPagedInventory().getId().equals("material_choose")))return;
		MaterialEditor.getInstance().cvarsEdited.remove(e.getPlayer().getName());
	}
	
}

