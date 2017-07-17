package io.github.gronnmann.coinflipper.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.Main;
import io.github.gronnmann.coinflipper.MaterialsManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.coinflipper.hook.HookProtocolLib;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.signinput.coinflipper.SignInputEvent;

public class CreationGUI implements Listener{
	private CreationGUI(){}
	
	private Inventory preset;
	private static CreationGUI gui = new CreationGUI();
	public static CreationGUI getInstance(){
		return gui;
	}
	
	private int BET_FINALIZE = 44, BET_AMOUNT = 8, BET_SIDE = 26;
	private int MON_1 = 0, MON_10 = 1, MON_100 = 2, MON_1000 = 3, MON_10000 = 4, MON_100000 = 6, MON_CUSTOM = 5;
	private int SIDE_HEADS = 18, SIDE_TAILS = 19;
	private int BACK = 36;
	
	
	public void generatePreset(){
		preset = Bukkit.createInventory(null, 45, MessagesManager.getMessage(Message.CREATION_NAME));
		
		preset.setItem(BET_AMOUNT, ItemUtils.createItem(MaterialsManager.getMaterial("creation_money_value"), MessagesManager.getMessage(Message.CREATION_MONEY).replaceAll("%MONEY%", 0+""), MaterialsManager.getData("creation_money_value")));
		preset.setItem(BET_SIDE, ItemUtils.createItem(MaterialsManager.getMaterial("creation_side_tails"), ChatColor.BLUE + MessagesManager.getMessage(Message.CREATION_SIDE).replaceAll("%SIDE%", "TAILS"),MaterialsManager.getData("creation_side_tails")));
		preset.setItem(BET_FINALIZE, ItemUtils.createItem(Material.SKULL_ITEM, ChatColor.BLUE + ChatColor.BOLD.toString() +"Bet", 3));
		
		String howToAdd = MessagesManager.getMessage(Message.CREATION_MONEY_LEFTTOADD);
		String howToRemove = MessagesManager.getMessage(Message.CREATION_MONEY_RIGHTTOREMOVE);
		
		String c = MessagesManager.getMessage(Message.CREATION_MONEY_COLOR);
		
		preset.setItem(MON_1, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_T1), MaterialsManager.getData("creation_money")), howToAdd), howToRemove));
		preset.setItem(MON_10, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_T2), MaterialsManager.getData("creation_money")), howToAdd), howToRemove));
		preset.setItem(MON_100, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_T3), MaterialsManager.getData("creation_money")), howToAdd), howToRemove));
		preset.setItem(MON_1000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_T4), MaterialsManager.getData("creation_money")), howToAdd), howToRemove));
		preset.setItem(MON_10000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_T5), MaterialsManager.getData("creation_money")), howToAdd), howToRemove));
		preset.setItem(MON_100000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_MAX), MaterialsManager.getData("creation_money")), howToAdd), howToRemove));
		
		preset.setItem(MON_CUSTOM,ItemUtils.addToLore(ItemUtils.createItem(MaterialsManager.getMaterial("creation_money"), c + MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM), MaterialsManager.getData("creation_money")), MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_DESC)));
		
		preset.setItem(SIDE_HEADS, ItemUtils.createItem(MaterialsManager.getMaterial("creation_side_heads"), ChatColor.AQUA + MessagesManager.getMessage(Message.HEADS).toUpperCase(), MaterialsManager.getData("creation_side_heads")));
		preset.setItem(SIDE_TAILS, ItemUtils.createItem(MaterialsManager.getMaterial("creation_side_tails"), ChatColor.AQUA + MessagesManager.getMessage(Message.TAILS).toUpperCase(), MaterialsManager.getData("creation_side_tails")));
		
		preset.setItem(BACK, ItemUtils.createItem(MaterialsManager.getMaterial("back"), MessagesManager.getMessage(Message.ANIMATION_FRAMEEDITOR_BACK), MaterialsManager.getData("back")));
	}
	
	private double getMaxMoney(String player){
		double use = ConfigManager.getManager().getConfig().getDouble("max_amount");
		double plMon = Main.getEcomony().getBalance(player);
		
		if (plMon < use){
			use = plMon;
		}
		return use;
		
	}
	
	
	private HashMap<String, CreationData> data = new HashMap<String, CreationData>();
	
	private ArrayList<String> customMon = new ArrayList<String>();
	
	
	@EventHandler
	public void mapRemover(PlayerQuitEvent e){
		if (data.containsKey(e.getPlayer().getName())){
			data.remove(e.getPlayer().getName());
		}
	}
	@EventHandler
	public void mapRemover2(InventoryCloseEvent e){
		if (customMon.contains(e.getPlayer().getName()))return;
		
		if (e.getInventory().getName().equals(MessagesManager.getMessage(Message.CREATION_NAME))){
			data.remove(e.getPlayer().getName());
		}
	}
	
	public void openInventory(Player player){
		
		
		if (data.containsKey(player.getName())){
			data.remove(player.getPlayer().getName());
		}
		
		Inventory pInv = Bukkit.createInventory(null, 45, MessagesManager.getMessage(Message.CREATION_NAME));
		pInv.setContents(preset.getContents());
		pInv.setItem(BET_FINALIZE, ItemUtils.setName(ItemUtils.getSkull(player.getName()), ChatColor.BLUE + ChatColor.BOLD.toString() + "Bet"));
		
		CreationData crData = new CreationData(pInv);
		
		data.put(player.getName(), crData);
		
		refreshInventory(player);
		
		player.openInventory(pInv);
	}
	
	
	
	@EventHandler
	public void clickManager(InventoryClickEvent e){
		if (e.getClickedInventory() == null)return;
		if (!e.getClickedInventory().getName().equals(MessagesManager.getMessage(Message.CREATION_NAME)))return;
		e.setCancelled(true);
		CreationData data = this.data.get(e.getWhoClicked().getName());
		if (data == null)return;
		
		if (e.getSlot() <= 4){
			double money = Double.valueOf(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName())
					.replaceAll("[^\\d]", ""));
			
			if (e.isRightClick()){
				data.setMoney(data.getMoney()-money);
			}else{
				data.setMoney(data.getMoney()+money);
			}
		}
		if (e.getSlot() == MON_CUSTOM){
			customMon.add(e.getWhoClicked().getName());
			e.getWhoClicked().sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_SPEC));
			e.getWhoClicked().closeInventory();
			
			if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigManager.getManager().getConfig().getBoolean("sign_input")){
				HookProtocolLib.getHook().openSignInput((Player) e.getWhoClicked());
			}
			
			return;
			
		}else if (e.getSlot() == MON_100000){
			if (e.isRightClick()){
				data.setMoney(0);
			}else{
				data.setMoney(this.getMaxMoney(e.getWhoClicked().getName()));
			}
		}
		
		
		else if (e.getSlot() == SIDE_HEADS){
			data.setSide(1);
		}else if (e.getSlot() == SIDE_TAILS){
			data.setSide(0);
		}else if (e.getSlot() == BET_FINALIZE){
			GamesManager.getManager().createGame((Player) e.getWhoClicked(), data.getSide(), data.getMoney());
		}else if (e.getSlot() == BACK){
			SelectionScreen.getInstance().openGameManager((Player) e.getWhoClicked());
		}
		
		this.refreshInventory((Player) e.getWhoClicked());
		
	}
	
	private void refreshInventory(Player player){
		CreationData data = this.data.get(player.getName());
		if (data == null)return;
		
		Inventory inv = data.getInventory();
		
		String side = String.valueOf(data.getSide()).replaceAll("0", MessagesManager.getMessage(Message.TAILS).toUpperCase())
				.replaceAll("1", MessagesManager.getMessage(Message.HEADS).toUpperCase());
		
		if (data.getSide() == 0){
			inv.setItem(BET_SIDE, ItemUtils.createItem(MaterialsManager.getMaterial("creation_side_tails"), MessagesManager.getMessage(Message.CREATION_SIDE).replaceAll("%SIDE%", side), MaterialsManager.getData("creation_side_tails")));
		}else{
			inv.setItem(BET_SIDE, ItemUtils.createItem(MaterialsManager.getMaterial("creation_side_heads"), MessagesManager.getMessage(Message.CREATION_SIDE).replaceAll("%SIDE%", side), MaterialsManager.getData("creation_side_heads")));
		}

		
		ItemUtils.setName(inv.getItem(BET_AMOUNT), MessagesManager.getMessage(Message.CREATION_MONEY).replaceAll("%MONEY%", GeneralUtils.getFormattedNumbers(data.getMoney())));
		
		ItemStack headNew = ItemUtils.getSkull(player.getName());
		ItemUtils.setName(headNew, MessagesManager.getMessage(Message.MENU_HEAD_GAME).replaceAll("%ID%", ""));
		ItemUtils.addToLore(headNew, MessagesManager.getMessage(Message.MENU_HEAD_PLAYER).replaceAll("%PLAYER%", player.getName()));
		ItemUtils.addToLore(headNew, MessagesManager.getMessage(Message.MENU_HEAD_MONEY).replaceAll("%MONEY%", GeneralUtils.getFormattedNumbers(data.getMoney())));
		ItemUtils.addToLore(headNew, MessagesManager.getMessage(Message.MENU_HEAD_SIDE).replaceAll("%SIDE%", side));
		
		inv.setItem(BET_FINALIZE, headNew);
		
	}
	
	@EventHandler
	public void protocolLibHookInput(SignInputEvent e){
		
		if (!HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigManager.getManager().getConfig().getBoolean("sign_input"))return;
		
		Player p = e.getPlayer();
		if (!customMon.contains(p.getName()) || !data.containsKey(p.getName()))return;
		try{
			double mon = Double.parseDouble(e.getLine(0));
			
			if (mon < ConfigManager.getManager().getConfig().getDouble("min_amount")){
				p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_TOOLITTLE));
				HookProtocolLib.getHook().openSignInput(e.getPlayer());
				return;
			}else if (mon > ConfigManager.getManager().getConfig().getDouble("max_amount")){
				p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_TOOMUCH));
				HookProtocolLib.getHook().openSignInput(e.getPlayer());
				return;
			}
			if (mon > Main.getEcomony().getBalance(e.getPlayer())){
				p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_NOMONEY));
				HookProtocolLib.getHook().openSignInput(e.getPlayer());
				return;
			}
			
			data.get(p.getName()).setMoney(mon);
			
			
			
			p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_SUCCESS).replaceAll("%MONEY%", mon+""));
			
			p.openInventory(data.get(p.getName()).getInventory());
			
			this.refreshInventory(p);
			customMon.remove(e.getPlayer().getName());
		}catch(Exception ex){
			p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_NOTNUM));
			HookProtocolLib.getHook().openSignInput(e.getPlayer());
		}
	}
	
	@EventHandler
	public void customValue(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if (!customMon.contains(p.getName()) || !data.containsKey(p.getName()))return;
		e.setCancelled(true);
		try{
			double mon = Double.parseDouble(e.getMessage());
			
			if (mon < ConfigManager.getManager().getConfig().getDouble("min_amount")){
				p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_TOOLITTLE));
				return;
			}else if (mon > ConfigManager.getManager().getConfig().getDouble("max_amount")){
				p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_TOOMUCH));
				return;
			}
			if (mon > Main.getEcomony().getBalance(e.getPlayer())){
				p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_NOMONEY));
				return;
			}
			
			data.get(p.getName()).setMoney(mon);
			
			
			
			p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_SUCCESS).replaceAll("%MONEY%", mon+""));
			
			p.openInventory(data.get(p.getName()).getInventory());
			
			this.refreshInventory(p);
			customMon.remove(e.getPlayer().getName());
		}catch(Exception ex){
			p.sendMessage(MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_NOTNUM));
		}
	}
}
