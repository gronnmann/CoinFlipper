package io.github.gronnmann.coinflipper.gui.configeditor;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class SoundChooser implements Listener{
	private SoundChooser(){}
	private static SoundChooser instance = new SoundChooser();
	public static SoundChooser getInstance(){
		return instance;
	}
	
	private PagedInventory selectionScreen;
	private FileConfiguration config;
	
		
	int TRUE = 3, FALSE = 5;
	
	public void setup(){
		config = ConfigManager.getManager().getConfig();
		
		selectionScreen = new PagedInventory("Select sound", ItemUtils.createItem(Material.ARROW, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_NEXT)),
				ItemUtils.createItem(Material.ARROW, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_PREV)),
				ItemUtils.createItem(Material.INK_SACK, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_BACK), 1),
				"sound_choose", ConfigEditor.getInstance().selectionScreen);
		
		for (Sound sound : Sound.values()){
			ItemStack item = ItemUtils.createItem(Material.NOTE_BLOCK, ChatColor.GOLD.toString() + sound.toString());
			
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Left-click to set sound.");
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Right-click for preview.");
			
			
			selectionScreen.addItem(item);
		}
		
	}
	
	public void refresh(String current){
		for (Inventory inv : selectionScreen.getPages()){ 
			for (ItemStack item : inv.getContents()){
				if (item == null)continue;
				if (!(item.getType().equals(Material.NOTE_BLOCK)))continue;
				String sound = ChatColor.stripColor(item.getItemMeta().getDisplayName());
				item.removeEnchantment(Enchantment.DIG_SPEED);
				
				if (item.getItemMeta().getLore().size() != 2){
					ItemUtils.setLore(item, ChatColor.YELLOW + "Left-click to set sound.");
					ItemUtils.addToLore(item, ChatColor.YELLOW + "Right-click for preview.");
				}
				
				if (current.equals(sound.toString())){
					item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
					
					ItemUtils.setLore(item, ChatColor.GREEN.toString() + ChatColor.ITALIC + "Currently selected");
					ItemUtils.addToLore(item, " ");
					ItemUtils.addToLore(item, ChatColor.YELLOW + "Left-click to set sound.");
					ItemUtils.addToLore(item, ChatColor.YELLOW + "Right-click for preview.");
					
					ItemMeta met = item.getItemMeta(); met.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(met);
				}
			}
		}
	}
	
	
	public void openEditor(Player p, String cvar){
		p.openInventory(selectionScreen.getPage(0));
	}
	
	@EventHandler
	public void selectNewSound(PagedInventoryClickEvent e){
		if (!(e.getPagedInventory().getId().equals("sound_choose")))return;
		
		
		ItemStack item = e.getCurrentItem();
		
		String sound = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		
		if (e.getClick().equals(ClickType.RIGHT)){
			e.getWhoClicked().playSound(e.getWhoClicked().getLocation(), Sound.valueOf(sound), 1, 1);
		}else if (e.getClick().equals(ClickType.LEFT)){
			ConfigEditor.getInstance().processEditing(e.getWhoClicked(), sound);
		}else return;
		
		
	}
	
	@EventHandler
	public void removeOnClose(PagedInventoryCloseEvent e){
		if (!(e.getPagedInventory().getId().equals("sound_choose")))return;
		ConfigEditor.getInstance().cvarsEdited.remove(e.getPlayer().getName());
	}
	
}

