package io.github.gronnmann.utils.coinflipper.input;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.coinflipper.input.InputData.InputType;
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
	
		
	int TRUE = 3, FALSE = 5;
	
	public void setup(){
		
		selectionScreen = new PagedInventory("Select sound", ItemUtils.createItem(Material.ARROW, Message.NEXT.getMessage()),
				ItemUtils.createItem(Material.ARROW, Message.PREVIOUS.getMessage()),
				ItemUtils.createItem(Material.INK_SACK, Message.BACK.getMessage(), 1),
				"sound_choose", null);
		
		for (Sound sound : Sound.values()){
			ItemStack item = ItemUtils.createItem(Material.NOTE_BLOCK, ChatColor.GOLD.toString() + sound.toString());
			
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Left-click to set sound.");
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Right-click for preview.");
			
			
			selectionScreen.addItem(item);
		}
		
	}
	
	public void openEditor(Player p, Inventory returnInv, String name) {
		PagedInventory clone = PagedInventory.fromClone(selectionScreen, name);
		clone.setReturnInventory(returnInv);
		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.openInventory(clone.getPage(0));
			}
		}.runTask(CoinFlipper.getMain());
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
	

	
	@EventHandler
	public void selectNewSound(PagedInventoryClickEvent e){
		if (!(e.getPagedInventory().getId().equals("sound_choose")))return;
		if (e.getSlot() >= PagedInventory.usableSlots)return;
		
		ItemStack item = e.getCurrentItem();
		
		String sound = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		
		if (e.getClick().equals(ClickType.RIGHT)){
			e.getWhoClicked().playSound(e.getWhoClicked().getLocation(), Sound.valueOf(sound), 1, 1);
		}else if (e.getClick().equals(ClickType.LEFT)){
			InputManager.processInput(e.getWhoClicked().getName(), sound);
		}else return;
		
		
	}
	
	@EventHandler
	public void removeMemoryLeak(PagedInventoryCloseEvent e){
		if (!(e.getPagedInventory().getId().equals("sound_choose")))return;
		
		if (InputManager.getData(e.getPlayer().getName()) != null && InputManager.getData(e.getPlayer().getName()).getType() == InputType.SOUND) {
			InputManager.removeInput(e.getPlayer().getName());
		}
	}
	
}

