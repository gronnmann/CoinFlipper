package io.github.gronnmann.coinflipper.gui.configurationeditor.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.gui.configurationeditor.FileEditSelector;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.coinflipper.hook.HookProtocolLib;
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
	private FileConfiguration config;
	
	
	
	int RELOAD, BACK;
	
	public void setup(Plugin pl){
		this.pl = pl;
		config = ConfigManager.getManager().getConfig();
		
		int howManySlots = 0;
		
		for (String cvars : config.getConfigurationSection("").getKeys(false)){
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
		
		for (String cvars : config.getConfigurationSection("").getKeys(false)){
			if (index > 54 )return;
			
			if (cvars.equals("config_version"))continue;
			
			
			selectionScreen.setItem(index, ItemUtils.setLore(ItemUtils.createItem(Material.THIN_GLASS, ChatColor.GOLD + cvars), ChatColor.YELLOW + "Value: " + 
			ChatColor.GREEN + config.getString(cvars)));
			index++;
		}
		
		selectionScreen.setItem(RELOAD, ItemUtils.createItem(Material.STAINED_GLASS_PANE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RELOAD", 4));
		selectionScreen.setItem(BACK, ItemUtils.createItem(Material.STAINED_GLASS_PANE, MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_BACK), 14));
		
	}
	
	private void refresh(){
		for (ItemStack item : selectionScreen.getContents()){
			
			if (item == null)continue;
			
			if (item.getType().equals(Material.STAINED_GLASS_PANE))continue;
			
			String value = config.getString(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
			
			ItemUtils.setLore(item, ChatColor.YELLOW + "Value: " + ChatColor.GREEN + value);
		}
	}
	
	public void openEditor(final Player p){
		Bukkit.getScheduler().runTask(CoinFlipper.getMain(), ()->{
			p.openInventory(selectionScreen);
		});
	}
	
	
	
	public HashMap<String, String> cvarsEdited = new HashMap<String, String>();
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
		
		String value = config.getString(cvar);
		
		cvarsEdited.put(p.getName(), cvar);
		
		e.getWhoClicked().sendMessage(MessagesManager.getMessage(Message.CONFIGURATOR_SPEC).replaceAll("%CVAR%", e.getCurrentItem().getItemMeta().getDisplayName()));
		e.getWhoClicked().closeInventory();
		
		if (cvar.equals("sound_while_choosing") || cvar.equals("sound_winner_chosen")){
			SoundChooser.getInstance().refresh(config.getString(cvar));
			SoundChooser.getInstance().openEditor(p, cvar);
			return;
		}
		
		if (GeneralUtils.isBoolean(value)){
			BooleanChooser.getInstance().openEditor(p, cvar);
			return;
		}
		
		
		
		if (GeneralUtils.isDouble(value)){
			editingNumbers.add(p.getName());
		}
		
		if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigManager.getManager().getConfig().getBoolean("sign_input") && !cvar.equals("disabled_worlds")){
			HookProtocolLib.getHook().openSignInput(p);
		}
		
	}
	
	@EventHandler
	public void protocolLibHookInput(SignInputEvent e){
		
		if (!HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigManager.getManager().getConfig().getBoolean("sign_input"))return;
		
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
		
		
		
		System.out.println("[CoinFlipper] " + p.getName() + " changed cvar " + cvarsEdited.get(p.getName()) + " value from " + 
		config.getString(cvarsEdited.get(p.getName())) + " to " + newValue);
		
		if (editingNumbers.contains(p.getName())){
			if (GeneralUtils.isDouble(newValue)){
				editingNumbers.remove(p.getName());
				if (GeneralUtils.isInt(newValue)){
					config.set(cvarsEdited.get(p.getName()), Integer.parseInt(newValue));
				}else{
					config.set(cvarsEdited.get(p.getName()), Double.parseDouble(newValue));
				}
				
			}else{
				p.sendMessage(MessagesManager.getMessage(Message.INPUT_NOTNUM));
				if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigManager.getManager().getConfig().getBoolean("sign_input")){
					HookProtocolLib.getHook().openSignInput(p);
				}
				return;
			}
		}
		else if (GeneralUtils.isBoolean(newValue)){
			config.set(cvarsEdited.get(p.getName()), Boolean.valueOf(newValue));
		}else{
			config.set(cvarsEdited.get(p.getName()), newValue);
		}
		
		
		ConfigManager.getManager().saveConfig();
		p.sendMessage(MessagesManager.getMessage(Message.CONFIGURATOR_EDIT_SUCCESSFUL).replaceAll("%VALUE%", newValue).replace("%CVAR%", cvarsEdited.get(p.getName())));
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
