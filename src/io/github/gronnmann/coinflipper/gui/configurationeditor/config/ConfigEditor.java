package io.github.gronnmann.coinflipper.gui.configurationeditor.config;

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

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.gui.configurationeditor.FileEditSelector;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.coinflipper.input.InputData;
import io.github.gronnmann.utils.coinflipper.input.InputData.InputType;
import io.github.gronnmann.utils.coinflipper.input.InputManager;
import io.github.gronnmann.utils.coinflipper.input.PlayerInputEvent;

public class ConfigEditor implements Listener{
	private ConfigEditor(){}
	private static ConfigEditor instance = new ConfigEditor();
	public static ConfigEditor getInstance(){
		return instance;
	}
	
	private Plugin pl;
	protected Inventory selectionScreen;
	
	
	
	int RELOAD, BACK;
	
	public void setup(){
		this.pl = CoinFlipper.getMain();
		
		int howManySlots = 0;
		
		for (ConfigVar cvars : ConfigVar.values()){
			howManySlots++;
		}
		
		int size = ((howManySlots/9)+2)*9;
		
		if (size > 54){
			size = 54;
		}
		
		selectionScreen = Bukkit.createInventory(new ConfigEditorHolder(), size, "CoinFlipper config.yml");
		
		
		RELOAD = size-2;
		BACK = size-1;
		
		
		int index = 0;
		
		for (ConfigVar cvarsSrc : ConfigVar.values()){
			String cvars = cvarsSrc.getPath();
			if (index > 54 )return;

			ItemStack editItem = ItemUtils.setLore(ItemUtils.createItem(Material.THIN_GLASS, ChatColor.GOLD + cvars), ChatColor.YELLOW + "Value: " + 
					ChatColor.GREEN + cvarsSrc.getString());
			ItemUtils.addToLore(editItem, ChatColor.GREEN + cvarsSrc.getDescription());
			
			selectionScreen.setItem(index, editItem);
			index++;
		}
		
		selectionScreen.setItem(RELOAD, ItemUtils.createItem(Material.STAINED_GLASS_PANE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RELOAD", 4));
		selectionScreen.setItem(BACK, ItemUtils.createItem(Material.STAINED_GLASS_PANE, Message.BACK.getMessage(), 14));
		
	}
	
	private void refresh(){
		for (ItemStack item : selectionScreen.getContents()){
			
			if (item == null)continue;
			
			if (item.getType().equals(Material.STAINED_GLASS_PANE))continue;
			
			String value = ConfigVar.fromPath(ChatColor.stripColor(item.getItemMeta().getDisplayName())).getString();
			
			ItemUtils.setLore(item, ChatColor.YELLOW + "Value: " + ChatColor.GREEN + value);
		}
	}
	
	public void openEditor(final Player p){
		Bukkit.getScheduler().runTask(CoinFlipper.getMain(), ()->{
			p.openInventory(selectionScreen);
		});
	}
	
	
	
	private static final String CONFIG_EDITCVAR = "CONFIG_EDITCVAR";
	
	@EventHandler
	public void clickDetector(InventoryClickEvent e){
		if (e.getClickedInventory() == null)return;
		if (!(e.getClickedInventory().getHolder() instanceof ConfigEditorHolder))return;
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
		
		String cvarS = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
		ConfigVar cvar = ConfigVar.fromPath(cvarS);
		
		String value = cvar.getDefaultValue().toString();
		
		
		
		e.getWhoClicked().sendMessage(Message.CONFIGURATOR_SPEC.getMessage().replace("%CVAR%", e.getCurrentItem().getItemMeta().getDisplayName()));
		e.getWhoClicked().closeInventory();
		
		
		
		InputType inputType = InputType.STRING;
		
		if (GeneralUtils.isBoolean(value)){
			inputType = InputType.BOOLEAN;
		}
		else if (GeneralUtils.isDouble(value)) {
			if (GeneralUtils.isInt(value)) {
				inputType = InputType.INTEGER;
			}else {
				inputType = InputType.DOUBLE;
			}
			
		}
		
		if (cvarS.equals(ConfigVar.SOUND_WHILE_CHOOSING.getPath()) || cvarS.equals(ConfigVar.SOUND_WINNER_CHOSEN.getPath())){
			inputType = InputType.SOUND;
		}
		
		
		InputData data = new InputData(CONFIG_EDITCVAR, inputType);
		data.addExtraData("CVAR", cvar);
		if (inputType == InputType.BOOLEAN || inputType == InputType.SOUND) {
			data.addExtraData("RETURN_INVENTORY", selectionScreen);
			data.addExtraData("INVENTORY_NAME", cvarS);
		}
		
		InputManager.requestInput(p.getName(), data);
		
	}
	
	
	@EventHandler
	public void handleInput(PlayerInputEvent e) {
		InputData params = e.getParams();
		Player p = e.getPlayer();
		
		if (!(params.getId().equals(CONFIG_EDITCVAR)))return;
			
		if (e.isExiting()) {
			openEditor(p);
			return;
		}
		
		
		Object newValue = e.getData().toString();
		
		ConfigVar cvar = (ConfigVar) params.getExtraData("CVAR");
		
		if (cvar == null) {
			Debug.print("Player edited cvar but no cvar data found.");
			return;
		}
		
		System.out.println("[CoinFlipper] " + p.getName() + " changed cvar " + cvar + " value from " + 
				cvar.getString() + " to " + newValue);
		
		cvar.setValue(newValue);
		
		ConfigManager.getManager().saveConfig();
		p.sendMessage(Message.CONFIGURATOR_EDIT_SUCCESSFUL.getMessage().replace("%VALUE%", newValue.toString()).replace("%CVAR%", cvar.getPath()));
		refresh();
		openEditor(p);
	}
	
	@EventHandler
	public void cancelDrag(InventoryDragEvent e) {
		if (e.getInventory().getHolder() instanceof ConfigEditorHolder)e.setCancelled(true);
	}
}

class ConfigEditorHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
