package io.github.gronnmann.coinflipper.gui.configurationeditor.materials;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.gui.configurationeditor.FileEditSelector;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.ItemUtils;

public class MaterialEditor implements Listener{
	private MaterialEditor(){}
	private static MaterialEditor instance = new MaterialEditor();
	public static MaterialEditor getInstance(){
		return instance;
	}
	
	private Plugin pl;
	protected Inventory selectionScreen;
	private FileConfiguration config;
	
	
	
	int RELOAD, BACK;
	
	public void setup(Plugin pl){
		this.pl = pl;
		config = ConfigManager.getManager().getMaterials();
		
		int howManySlots = 0;
		
		for (String cvars : config.getConfigurationSection("").getKeys(false)){
			if (!cvars.endsWith("_data")){
				howManySlots++;
			}
		}
		
		int size = ((howManySlots/9)+2)*9;
		
		if (size > 54){
			size = 54;
		}
		
		selectionScreen = Bukkit.createInventory(new MaterialsEditorHolder(), size, "CoinFlipper materials.yml");
		
		
		RELOAD = size-2;
		BACK = size-1;
		
		
		int index = 0;
		
		for (String cvars : config.getConfigurationSection("").getKeys(false)){
			if (cvars.endsWith("_data"))continue;
			if (index > 54 )return;
			
			Material toUse = Material.valueOf(config.getString(cvars));
			int data = config.getInt(cvars+"_data");
			
			
			selectionScreen.setItem(index, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(toUse, ChatColor.GOLD + cvars, data), ChatColor.YELLOW + "Material: " + 
			ChatColor.GREEN + config.getString(cvars)), ChatColor.YELLOW + "Data: " + 
			ChatColor.GREEN + config.getInt(cvars+"_data")));
			index++;
		}
		
		selectionScreen.setItem(RELOAD, ItemUtils.createItem(Material.LEGACY_STAINED_GLASS_PANE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RELOAD", 4));
		selectionScreen.setItem(BACK, ItemUtils.createItem(Material.LEGACY_STAINED_GLASS_PANE, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_BACK), 14));
		
	}
	
	private void refresh(){
		for (ItemStack item : selectionScreen.getContents()){
			
			if (item == null)continue;
			
			if (item.getType().equals(Material.LEGACY_STAINED_GLASS_PANE))continue;
			
			String value = config.getString(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
			
			ItemUtils.setLore(item, ChatColor.YELLOW + "Value: " + ChatColor.GREEN + value);
		}
	}
	
	public void openEditor(Player p){
		p.openInventory(selectionScreen);
	}
	
	
	
	public HashMap<String, String> cvarsEdited = new HashMap<String, String>();
	
	@EventHandler
	public void clickDetector(InventoryClickEvent e){
		if (e.getClickedInventory() == null)return;
		if (!(e.getClickedInventory().getHolder() instanceof MaterialsEditorHolder))return;
		e.setCancelled(true);
		
		Player p = (Player)e.getWhoClicked();
		
		if (e.getCurrentItem().getType().equals(Material.AIR))return;
		
		
		//Reload
		if (e.getSlot() == RELOAD){
			if (!p.hasPermission("coinflipper.reload")){
				p.sendMessage(MessagesManager.getMessage(Message.NO_PERMISSION));
				return;
			}
			System.out.println("[CoinFlipper] Attempting to reload CoinFlipper (requested by " + p.getName() + ")");
			ConfigManager.getManager().reload();
			
			p.sendMessage(MessagesManager.getMessage(Message.RELOAD_SUCCESS));
			
			openEditor(p);
			
			return;
		}else if (e.getSlot() == BACK){
			FileEditSelector.getInstance().openConfigurator(p);
			return;
		}
		
		String cvar = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
		
		String value = e.getCurrentItem().getType().toString();
		
		Debug.print("Editing: " + cvar + ":" + value);
		
		
		
		cvarsEdited.put(p.getName(), cvar);
		
		e.getWhoClicked().sendMessage(MessagesManager.getMessage(Message.CONFIGURATOR_SPEC).replaceAll("%CVAR%", e.getCurrentItem().getItemMeta().getDisplayName()));
		e.getWhoClicked().closeInventory();
		
		MaterialChooser.getInstance().openEditor(p, cvar);
	}
	
	
	public void processEditing(Player p, ItemStack newValue){
		
		
		
		System.out.println("[CoinFlipper] " + p.getName() + " changed material " + cvarsEdited.get(p.getName()) + " value from " + 
		config.getString(cvarsEdited.get(p.getName())) + " to " + newValue.getData().toString());
		
		
		config.set(cvarsEdited.get(p.getName()), newValue.getType().toString());
		
		config.set(cvarsEdited.get(p.getName())+"_data", newValue.getDurability());
		
		Debug.print("Editing cvar: " + cvarsEdited.get(p.getName()) + " Material: " + newValue.getType().toString() + ", Data: " + newValue.getDurability());
		
		setup(pl);
		
		ConfigManager.getManager().saveMaterials();
		p.sendMessage(MessagesManager.getMessage(Message.CONFIGURATOR_EDIT_SUCCESSFUL).
				replaceAll("%VALUE%", newValue.getData().toString()).replace("%CVAR%", cvarsEdited.get(p.getName())));
		cvarsEdited.remove(p.getName());
		
		refresh();
		openEditor(p);
	}
	
}

class MaterialsEditorHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
