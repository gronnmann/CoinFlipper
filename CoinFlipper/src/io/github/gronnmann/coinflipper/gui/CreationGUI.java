package io.github.gronnmann.coinflipper.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.utils.GeneralUtils;
import io.github.gronnmann.utils.ItemUtils;

public class CreationGUI implements Listener{
	private CreationGUI(){}
	
	private Inventory preset;
	private static CreationGUI gui = new CreationGUI();
	public static CreationGUI getInstance(){
		return gui;
	}
	
	private int BET_FINALIZE = 44, BET_AMOUNT = 8, BET_SIDE = 26;
	private int MON_1 = 0, MON_10 = 1, MON_100 = 2, MON_1000 = 3, MON_10000 = 4, MON_100000 = 5, MON_CUSTOM = 6;
	private int SIDE_HEADS = 18, SIDE_TAILS = 19;
	
	
	public void generatePreset(){
		preset = Bukkit.createInventory(null, 45, MessagesManager.getMessage(Message.CREATION_NAME));
		
		preset.setItem(BET_AMOUNT, ItemUtils.createItem(Material.EMERALD, MessagesManager.getMessage(Message.CREATION_MONEY).replaceAll("%MONEY%", 0+"")));
		preset.setItem(BET_SIDE, ItemUtils.createItem(Material.WOOL, ChatColor.BLUE + MessagesManager.getMessage(Message.CREATION_SIDE).replaceAll("%SIDE%", "TAILS")));
		preset.setItem(BET_FINALIZE, ItemUtils.createItem(Material.SKULL_ITEM, ChatColor.BLUE + ChatColor.BOLD.toString() +"Bet", 3));
		
		String howToAdd = MessagesManager.getMessage(Message.CREATION_MONEY_LEFTTOADD);
		String howToRemove = MessagesManager.getMessage(Message.CREATION_MONEY_RIGHTTOREMOVE);
		
		String c = MessagesManager.getMessage(Message.CREATION_MONEY_COLOR);
		
		preset.setItem(MON_1, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_T1)), howToAdd), howToRemove));
		preset.setItem(MON_10, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_T2)), howToAdd), howToRemove));
		preset.setItem(MON_100, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_T3)), howToAdd), howToRemove));
		preset.setItem(MON_1000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_T4)), howToAdd), howToRemove));
		preset.setItem(MON_10000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_T5)), howToAdd), howToRemove));
		preset.setItem(MON_100000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_T6)), howToAdd), howToRemove));
		
		preset.setItem(MON_CUSTOM,ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, c + MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM)), MessagesManager.getMessage(Message.CREATION_MONEY_CUSTOM_DESC)));
		
		preset.setItem(SIDE_HEADS, ItemUtils.createItem(Material.WOOL, ChatColor.AQUA + MessagesManager.getMessage(Message.HEADS).toUpperCase(), 15));
		preset.setItem(SIDE_TAILS, ItemUtils.createItem(Material.WOOL, ChatColor.AQUA + MessagesManager.getMessage(Message.TAILS).toUpperCase(), 0));
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
		
		if (e.getSlot() <= 5){
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
			return;
			
		}else if (e.getSlot() == SIDE_HEADS){
			data.setSide(1);
		}else if (e.getSlot() == SIDE_TAILS){
			data.setSide(0);
		}else if (e.getSlot() == BET_FINALIZE){
			GamesManager.getManager().createGame((Player) e.getWhoClicked(), data.getSide(), data.getMoney());
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
			inv.setItem(BET_SIDE, ItemUtils.createItem(Material.WOOL, MessagesManager.getMessage(Message.CREATION_SIDE).replaceAll("%SIDE%", side), 0));
		}else{
			inv.setItem(BET_SIDE, ItemUtils.createItem(Material.WOOL, MessagesManager.getMessage(Message.CREATION_SIDE).replaceAll("%SIDE%", side), 15));
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
	public void customValue(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if (!customMon.contains(p.getName()) || !data.containsKey(p.getName()))return;
		e.setCancelled(true);
		try{
			double mon = Double.parseDouble(e.getMessage());
			
			data.get(p.getName()).setMoney(mon);
			
			p.sendMessage(ChatColor.GREEN + "Bet money sat to: " + ChatColor.YELLOW + "$" + mon);
			
			p.openInventory(data.get(p.getName()).getInventory());
			
			this.refreshInventory(p);
			customMon.remove(e.getPlayer().getName());
		}catch(Exception ex){
			p.sendMessage(ChatColor.RED + "Please specify a number.");
		}
	}
}
