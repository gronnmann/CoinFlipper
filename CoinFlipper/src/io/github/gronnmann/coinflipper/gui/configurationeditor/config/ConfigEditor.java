package io.github.gronnmann.coinflipper.gui.configurationeditor.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.gui.configurationeditor.FileEditSelector;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.coinflipper.hook.HookProtocolLib;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.signinput.coinflipper.SignInputEvent;

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
		selectionScreen.setItem(BACK, ItemUtils.createItem(Material.STAINED_GLASS_PANE, Message.ANIMATION_FRAMEEDITOR_BACK.getMessage(), 14));
		
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
	
	
	
	public HashMap<String, ConfigVar> cvarsEdited = new HashMap<String, ConfigVar>();
	private ArrayList<String> editingNumbers = new ArrayList<String>();
	
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
		
		String value = cvar.getString();
		
		cvarsEdited.put(p.getName(), cvar);
		
		e.getWhoClicked().sendMessage(Message.CONFIGURATOR_SPEC.getMessage().replaceAll("%CVAR%", e.getCurrentItem().getItemMeta().getDisplayName()));
		e.getWhoClicked().closeInventory();
		
		if (cvarS.equals(ConfigVar.SOUND_WHILE_CHOOSING.getPath()) || cvarS.equals(ConfigVar.SOUND_WINNER_CHOSEN.getPath())){
			SoundChooser.getInstance().refresh(value);
			SoundChooser.getInstance().openEditor(p);
			return;
		}
		
		if (GeneralUtils.isBoolean(value)){
			BooleanChooser.getInstance().openEditor(p, cvar);
			return;
		}
		
		
		
		if (GeneralUtils.isDouble(value)){
			editingNumbers.add(p.getName());
		}
		
		if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean() && !(cvar == ConfigVar.DISABLED_WORLDS)){
			HookProtocolLib.getHook().openSignInput(p);
		}
		
	}
	
	@EventHandler
	public void protocolLibHookInput(SignInputEvent e){
		
		if (!HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean())return;
		
		Player p = e.getPlayer();
		if (!cvarsEdited.containsKey(p.getName()))return;
		
		processEditing(p,e.getLine(0));
	}
	
	@EventHandler
	public void customValue(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if (!cvarsEdited.containsKey(p.getName()))return;
		e.setCancelled(true);
		if (e.getMessage().equals("exit")){
			cvarsEdited.remove(p.getName());
			openEditor(p);
			return;
		}
		processEditing(p, e.getMessage());
	}
	
	public void processEditing(Player p, String newValue){
		
		if (newValue.equalsIgnoreCase("cancel")) {
			cvarsEdited.remove(p.getName());
			openEditor(p);
			return;
		}
		
		ConfigVar cvar = cvarsEdited.get(p.getName());
		
		if (cvar == null) {
			Debug.print("Player edited cvar but not in cvar editor list.");
			return;
		}
		
		System.out.println("[CoinFlipper] " + p.getName() + " changed cvar " + cvarsEdited.get(p.getName()) + " value from " + 
			cvar.getString() + " to " + newValue);
		
		if (editingNumbers.contains(p.getName())){
			if (GeneralUtils.isDouble(newValue)){
				editingNumbers.remove(p.getName());
				if (GeneralUtils.isInt(newValue)){
					cvar.setValue(Integer.parseInt(newValue));
				}else{
					cvar.setValue(Double.parseDouble(newValue));
				}
				
			}else{
				p.sendMessage(Message.INPUT_NOTNUM.getMessage());
				if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean()){
					HookProtocolLib.getHook().openSignInput(p);
				}
				return;
			}
		}
		else if (GeneralUtils.isBoolean(newValue)){
			cvar.setValue(Boolean.valueOf(newValue));
		}else{
			cvar.setValue(newValue);
		}
		
		
		ConfigManager.getManager().saveConfig();
		p.sendMessage(Message.CONFIGURATOR_EDIT_SUCCESSFUL.getMessage().replaceAll("%VALUE%", newValue).replace("%CVAR%", cvarsEdited.get(p.getName()).getPath()));
		cvarsEdited.remove(p.getName());
		
		refresh();
		openEditor(p);
	}
	
}

class ConfigEditorHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
