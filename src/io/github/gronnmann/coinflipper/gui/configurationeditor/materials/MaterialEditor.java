package io.github.gronnmann.coinflipper.gui.configurationeditor.materials;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.customizable.CustomMaterial;
import io.github.gronnmann.coinflipper.customizable.Message;
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
	
	
	
	int RELOAD, BACK;
	
	public void setup(Plugin pl){
		this.pl = pl;
		
		int howManySlots = 0;
		
		for (CustomMaterial materials : CustomMaterial.values()){
			howManySlots++;
		}
		
		int size = ((howManySlots/9)+2)*9;
		
		if (size > 54){
			size = 54;
		}
		
		selectionScreen = Bukkit.createInventory(new MaterialsEditorHolder(), size, "CoinFlipper materials.yml");
		
		
		RELOAD = size-2;
		BACK = size-1;
		
		
		int index = 0;
		
		for (CustomMaterial materials : CustomMaterial.values()){
			
			String cvars = materials.getPath();
			
			if (index > 54 )return;
			
			Material toUse = materials.getMaterial();
			int data = materials.getData();
			
			
			selectionScreen.setItem(index, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(toUse, ChatColor.GOLD + cvars, data), ChatColor.YELLOW + "Material: " + 
			ChatColor.GREEN + materials.getMaterial().toString()), ChatColor.YELLOW + "Data: " + 
			ChatColor.GREEN + materials.getData()+""));
			index++;
		}
		
		selectionScreen.setItem(RELOAD, ItemUtils.createItem(Material.STAINED_GLASS_PANE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RELOAD", 4));
		selectionScreen.setItem(BACK, ItemUtils.createItem(Material.STAINED_GLASS_PANE, Message.BACK.getMessage(), 14));
		
	}
	
	private void refresh(){
		for (ItemStack item : selectionScreen.getContents()){
			
			if (item == null)continue;
			
			if (item.getType().equals(Material.STAINED_GLASS_PANE))continue;
			
			String value = CustomMaterial.fromPath(ChatColor.stripColor(item.getItemMeta().getDisplayName())).getMaterial().toString();
			
			ItemUtils.setLore(item, ChatColor.YELLOW + "Value: " + ChatColor.GREEN + value);
		}
	}
	
	public void openEditor(Player p){
		p.openInventory(selectionScreen);
	}
	
	
	
	public HashMap<String, CustomMaterial> cvarsEdited = new HashMap<String, CustomMaterial>();
	
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
				p.sendMessage(Message.NO_PERMISSION.getMessage());
				return;
			}
			System.out.println("[CoinFlipper] Attempting to reload CoinFlipper (requested by " + p.getName() + ")");
			ConfigManager.getManager().reload();
			
			p.sendMessage(Message.RELOAD_SUCCESS.getMessage());
			
			openEditor(p);
			
			return;
		}else if (e.getSlot() == BACK){
			FileEditSelector.getInstance().openConfigurator(p);
			return;
		}
		
		String cvar = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
		
		String value = e.getCurrentItem().getType().toString();
		
		Debug.print("Editing: " + cvar + ":" + value);
		
		
		
		cvarsEdited.put(p.getName(), CustomMaterial.fromPath(cvar));
		
		e.getWhoClicked().sendMessage(Message.CONFIGURATOR_SPEC.getMessage().replace("%CVAR%", e.getCurrentItem().getItemMeta().getDisplayName()));
		e.getWhoClicked().closeInventory();
		
		MaterialChooser.getInstance().openEditor(p, cvar);
	}
	
	
	public void processEditing(Player p, ItemStack newValue){
		
		CustomMaterial cvar = cvarsEdited.get(p.getName());
		
		System.out.println("[CoinFlipper] " + p.getName() + " changed material " + cvarsEdited.get(p.getName()) + " value from " + 
		cvar.getMaterial().toString() + " to " + newValue.getData().toString());
		
		cvar.setMaterial(newValue.getType());
		cvar.setData(newValue.getDurability());
		
		Debug.print("Editing cvar: " + cvarsEdited.get(p.getName()) + " Material: " + newValue.getType().toString() + ", Data: " + newValue.getDurability());
		
		setup(pl);
		
		ConfigManager.getManager().saveMaterials();
		p.sendMessage(Message.CONFIGURATOR_EDIT_SUCCESSFUL.getMessage().
				replace("%VALUE%", newValue.getData().toString()).replace("%CVAR%", cvar.getPath()));
		cvarsEdited.remove(p.getName());
		
		refresh();
		openEditor(p);
	}
	
	@EventHandler
	public void cancelDrag(InventoryDragEvent e) {
		if (e.getInventory().getHolder() instanceof MaterialsEditorHolder)e.setCancelled(true);
	}
	
}

class MaterialsEditorHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
