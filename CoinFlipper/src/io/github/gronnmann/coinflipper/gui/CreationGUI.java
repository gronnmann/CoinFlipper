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
		preset = Bukkit.createInventory(null, 45, "CoinFlipper Creation");
		
		preset.setItem(BET_AMOUNT, ItemUtils.createItem(Material.EMERALD, ChatColor.BLUE + ChatColor.BOLD.toString() + "Money: " + ChatColor.AQUA + "0"));
		preset.setItem(BET_SIDE, ItemUtils.createItem(Material.WOOL, ChatColor.BLUE + ChatColor.BOLD.toString() +"Side: " + ChatColor.AQUA + "TAILS", 0));
		preset.setItem(BET_FINALIZE, ItemUtils.createItem(Material.SKULL_ITEM, ChatColor.BLUE + ChatColor.BOLD.toString() +"Bet", 3));
		
		String howToAdd = ChatColor.GREEN + "Left click to add this to your bet.";
		String howToRemove = ChatColor.RED + "Right click to subtract this from your bet.";
		
		preset.setItem(MON_1, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"$1"), howToAdd), howToRemove));
		preset.setItem(MON_10, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"$10"), howToAdd), howToRemove));
		preset.setItem(MON_100, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"$100"), howToAdd), howToRemove));
		preset.setItem(MON_1000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"$1000"), howToAdd), howToRemove));
		preset.setItem(MON_10000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"$10000"), howToAdd), howToRemove));
		preset.setItem(MON_100000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"$100000"), howToAdd), howToRemove));
		
		preset.setItem(MON_CUSTOM,ItemUtils.addToLore(ItemUtils.createItem(Material.EMERALD, ChatColor.AQUA +"Custom value"), ChatColor.GREEN + "Input custom value."));
		
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
		
		if (e.getInventory().getName().equals("CoinFlipper Creation")){
			data.remove(e.getPlayer().getName());
		}
	}
	
	public void openInventory(Player player){
		
		
		if (data.containsKey(player.getName())){
			data.remove(player.getPlayer().getName());
		}
		
		Inventory pInv = Bukkit.createInventory(null, 45, "CoinFlipper Creation");
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
		if (!e.getClickedInventory().getName().equals("CoinFlipper Creation"))return;
		e.setCancelled(true);
		CreationData data = this.data.get(e.getWhoClicked().getName());
		if (data == null)return;
		
		if (e.getSlot() <= 5){
			int money = Integer.valueOf(ChatColor.stripColor(e.getInventory().getItem(e.getSlot()).getItemMeta().getDisplayName())
					.replaceAll("[^\\d]", ""));
			
			if (e.isRightClick()){
				data.setMoney(data.getMoney()-money);
			}else{
				data.setMoney(data.getMoney()+money);
			}
		}
		if (e.getSlot() == MON_CUSTOM){
			customMon.add(e.getWhoClicked().getName());
			e.getWhoClicked().sendMessage(ChatColor.GREEN + "Please specify how much money you want to set your bet to!");
			e.getWhoClicked().closeInventory();
			return;
			
		}else if (e.getSlot() == SIDE_HEADS){
			data.setSide(1);
		}else if (e.getSlot() == SIDE_TAILS){
			data.setSide(0);
		}else if (e.getSlot() == BET_FINALIZE){
			BettingManager.getManager().createBet((Player) e.getWhoClicked(), data.getSide(), data.getMoney());
		}
		
		this.refreshInventory((Player) e.getWhoClicked());
		
	}
	
	private void refreshInventory(Player player){
		CreationData data = this.data.get(player.getName());
		if (data == null)return;
		
		Inventory inv = data.getInventory();
		
		String side = String.valueOf(data.getSide()).replaceAll("0", "TAILS").replaceAll("1", "HEADS");
		
		if (data.getSide() == 0){
			inv.setItem(BET_SIDE, ItemUtils.createItem(Material.WOOL, ChatColor.BLUE + ChatColor.BOLD.toString() + "Side: " + ChatColor.AQUA + side, 0));
		}else{
			inv.setItem(BET_SIDE, ItemUtils.createItem(Material.WOOL, ChatColor.BLUE + ChatColor.BOLD.toString() + "Side: " + ChatColor.AQUA + side, 15));
		}

		
		ItemUtils.setName(inv.getItem(BET_AMOUNT), ChatColor.BLUE + ChatColor.BOLD.toString() + "Money: " + ChatColor.AQUA + data.getMoney());
		
		ItemStack headNew = ItemUtils.getSkull(player.getName());
		ItemUtils.setName(headNew, MessagesManager.getMessage(Message.MENU_HEAD_GAME).replaceAll("%ID%", ""));
		ItemUtils.addToLore(headNew, MessagesManager.getMessage(Message.MENU_HEAD_PLAYER).replaceAll("%PLAYER%", player.getName()));
		ItemUtils.addToLore(headNew, MessagesManager.getMessage(Message.MENU_HEAD_MONEY).replaceAll("%MONEY%", GeneralUtils.getFormatted(data.getMoney())));
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
