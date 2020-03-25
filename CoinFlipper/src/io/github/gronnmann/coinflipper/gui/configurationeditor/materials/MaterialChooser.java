package io.github.gronnmann.coinflipper.gui.configurationeditor.materials;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.utils.coinflipper.Debug;
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
	private FileConfiguration config;
	
		
	int TRUE = 3, FALSE = 5;
	
	public void setup(){
		config = ConfigManager.getManager().getConfig();
		
		selectionScreen = new PagedInventory("Select material", ItemUtils.createItem(Material.ARROW, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_NEXT)),
				ItemUtils.createItem(Material.ARROW, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_PREV)),
				ItemUtils.createItem(Material.INK_SAC, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_BACK), 1),
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

