package io.github.gronnmann.coinflipper.gui.configurationeditor.messages;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.gui.configurationeditor.FileEditSelector;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.pagedinventory.coinflipper.PagedInventory;
import io.github.gronnmann.utils.pagedinventory.coinflipper.PagedInventoryClickEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageEditor implements Listener{
	private MessageEditor(){}
	private static MessageEditor instance = new MessageEditor();
	public static MessageEditor getInstance(){
		return instance;
	}
	
	
	/*
	 * Feature disabled due to bugs
	 * To enable:
	 * 	Uncomment in FileEditSelector.java
	 * 		L63
	 * 		L64
	 * 		L85-87
	 * 		L40
	 */
	
	private Plugin pl;
	protected PagedInventory selectionScreen;	
	
	
	private int RELOAD;
	
	public void setup(){
		this.pl = CoinFlipper.getMain();
		
		
		selectionScreen = new PagedInventory("CoinFlipper messages.yml", ItemUtils.createItem(Material.ARROW, Message.NEXT.getMessage()),
				ItemUtils.createItem(Material.ARROW, Message.PREVIOUS.getMessage()),
				ItemUtils.createItem(Material.INK_SACK, Message.BACK.getMessage(), 1),
				"coinflipper_messageeditor", FileEditSelector.getInstance().selectionScreen);
		
		
		RELOAD = 53;
		
		
		
		for (Message msgs : Message.values()){
			
			String cvars = msgs.toString();
			
			
			ItemStack item = ItemUtils.createItem(Material.PAPER, ChatColor.GOLD + cvars);
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Message: " + ChatColor.GREEN + msgs.getMessage());
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Default: " + ChatColor.GREEN + msgs.getDefaultMessage());
			
			ItemUtils.addToLore(item, "");
			
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Left-click to set message.");
			ItemUtils.addToLore(item, ChatColor.YELLOW + "Right-click to change to default.");
			
			selectionScreen.addItem(item);
			
		}
		
		for (Inventory inv : selectionScreen.getPages()){
			inv.setItem(RELOAD, ItemUtils.createItem(Material.STAINED_GLASS_PANE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RELOAD", 4));
		}
	}
	
	private void refresh(){
		for (Inventory inv : selectionScreen.getPages()){
			for (int i = 0; i <= PagedInventory.usableSlots-1; i++){
				ItemStack item = inv.getItem(i);
				
				if (item == null)continue;
				
				Message msg = Message.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
				if (msg == null)continue;
				
				
				ItemUtils.addToLore(item, ChatColor.YELLOW + "Message: " + ChatColor.GREEN + msg.getMessage());
				ItemUtils.addToLore(item, ChatColor.YELLOW + "Default: " + ChatColor.GREEN + msg.getDefaultMessage());
				
				
				ItemUtils.addToLore(item, "");
				
				ItemUtils.addToLore(item, ChatColor.YELLOW + "Left-click to set message.");
				ItemUtils.addToLore(item, ChatColor.YELLOW + "Right-click to change to default.");
			}
		}
	}
	
	public void openEditor(Player p){
		Bukkit.getScheduler().runTask(pl, ()->{			
			p.openInventory(selectionScreen.getPage(0));
		});
	}
	
	
	
	public HashMap<String, Message> cvarsEdited = new HashMap<String, Message>();
	public HashMap<String, String> ready = new HashMap<String, String>();
	
	
	@EventHandler
	public void clickDetector(PagedInventoryClickEvent e){
		if (!e.getPagedInventory().getId().equals("coinflipper_messageeditor"))return;
		
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
		}
		
		Message cvar = Message.valueOf(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
		if (cvar == null)return;
		
		cvarsEdited.put(p.getName(), cvar);
		
		if (e.isLeftClick()){
			e.getWhoClicked().sendMessage(Message.CONFIGURATOR_SPEC.getMessage().replace("%CVAR%", cvar.toString()));
			e.getWhoClicked().closeInventory();
		}else{
			processEditing((Player)e.getWhoClicked(), cvar.getDefaultMessage());
		}
		
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void handleEditing(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		String msg = e.getMessage();
		if (!cvarsEdited.containsKey(p.getName()))return;
		e.setCancelled(true);
		
		if (e.getMessage().equals("exit")){
			cvarsEdited.remove(p.getName());
			openEditor(p);
			return;
		}
		
		if (ready.containsKey(p.getName())){
			if (msg.equals("accept")){
				processEditing(p, ready.get(p.getName()));
				ready.remove(p.getName());
			}else if (msg.equals("change")){
				p.sendMessage(Message.CONFIGURATOR_SPEC.getMessage().replace("%CVAR%", cvarsEdited.get(p.getName()).toString()));
			}else{
				cvarsEdited.remove(p.getName());
				openEditor(p);
			}
			ready.remove(p.getName());
			return;
		}
		
		
		p.sendMessage("");
		p.sendMessage(Message.CONFIGURATOR_MESSAGE_PREVIEW.getMessage().replace("%CVAR%", cvarsEdited.get(p.getName()).toString()));
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
		p.sendMessage("");
		BaseComponent[] confirmArray = TextComponent.fromLegacyText(Message.CONFIGURATOR_MESSAGE_CONFIRM.getMessage());
		TextComponent confirm = new TextComponent();
		for (BaseComponent comp : confirmArray){
			confirm.addExtra(comp);
		}
		confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Message.CONFIGURATOR_MESSAGE_CONFIRM.getMessage())));
		confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "accept"));
		
		
		BaseComponent[] changeArray = TextComponent.fromLegacyText(Message.CONFIGURATOR_MESSAGE_CHANGE.getMessage());
		TextComponent change = new TextComponent();
		for (BaseComponent comp : changeArray){
			change.addExtra(comp);
		}
		change.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Message.CONFIGURATOR_MESSAGE_CHANGE.getMessage())));
		change.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "change"));
		
		
		BaseComponent[] cancelArray = TextComponent.fromLegacyText(Message.CONFIGURATOR_MESSAGE_CANCEL.getMessage());
		TextComponent cancel = new TextComponent();
		for (BaseComponent comp : cancelArray){
			cancel.addExtra(comp);
		}
		cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Message.CONFIGURATOR_MESSAGE_CANCEL.getMessage())));
		cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "cancel"));
		
		TextComponent combined = new TextComponent();
		combined.addExtra(confirm);
		combined.addExtra(" ");
		combined.addExtra(change);
		combined.addExtra(" ");
		combined.addExtra(cancel);
		
		
		//p.spigot().sendMessage(new ComponentBuilder(confirm.toLegacyText()).append(" ").append(change.toLegacyText()).append(" ").append(cancel.toLegacyText()).create());
		p.spigot().sendMessage(confirm);
		p.spigot().sendMessage(change);
		p.spigot().sendMessage(cancel);
		ready.put(p.getName(), msg);
	}
	
	
	public void processEditing(Player p, String newValue){
		
		Message cvar = cvarsEdited.get(p.getName());
		
		
		System.out.println("[CoinFlipper] " + p.getName() + " changed cvar " + cvarsEdited.get(p.getName()) + " value from " + 
				cvar.getMessage() + " to " + newValue);
		
		cvar.setMessage(newValue);		
		setup();
		
		cvar.save();
		p.sendMessage(Message.CONFIGURATOR_EDIT_SUCCESSFUL.getMessage().
				replace("%VALUE%", newValue).replace("%CVAR%", cvarsEdited.get(p.getName()).toString()));
		cvarsEdited.remove(p.getName());
		
		refresh();
		openEditor(p);
	}
	
	
	@EventHandler
	public void handleLeave(PlayerQuitEvent e) {
		cvarsEdited.remove(e.getPlayer().getName());
		ready.remove(e.getPlayer().getName());
	}
}

class MessagesEditorHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
