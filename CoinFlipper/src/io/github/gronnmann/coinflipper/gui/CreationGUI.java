package io.github.gronnmann.coinflipper.gui;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.CustomMaterial;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.coinflipper.hook.HookProtocolLib;
import io.github.gronnmann.utils.coinflipper.Debug;
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
		preset = Bukkit.createInventory(new CreationGUIHolder(), 45, Message.CREATION_NAME.getMessage());
		
		preset.setItem(BET_AMOUNT, ItemUtils.createItem(CustomMaterial.CREATION_MONEY_VALUE.getMaterial(), Message.CREATION_MONEY.getMessage().replaceAll("%MONEY%", 0+""), CustomMaterial.CREATION_MONEY_VALUE.getData()));
		preset.setItem(BET_SIDE, ItemUtils.createItem(CustomMaterial.CREATION_SIDE_TAILS.getMaterial(), ChatColor.BLUE + Message.CREATION_SIDE.getMessage().replaceAll("%SIDE%", "TAILS"),CustomMaterial.CREATION_SIDE_TAILS.getData()));
		preset.setItem(BET_FINALIZE, ItemUtils.createItem(Material.SKULL_ITEM, ChatColor.BLUE + ChatColor.BOLD.toString() +"Bet", 3));
		
		String howToAdd = Message.CREATION_MONEY_LEFTTOADD.getMessage();
		String howToRemove = Message.CREATION_MONEY_RIGHTTOREMOVE.getMessage();
		
		String c = Message.CREATION_MONEY_COLOR.getMessage();
		
		preset.setItem(MON_1, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_T1.getMessage(), CustomMaterial.CREATION_MONEY.getData()), howToAdd), howToRemove));
		preset.setItem(MON_10, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_T2.getMessage(), CustomMaterial.CREATION_MONEY.getData()), howToAdd), howToRemove));
		preset.setItem(MON_100, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_T3.getMessage(), CustomMaterial.CREATION_MONEY.getData()), howToAdd), howToRemove));
		preset.setItem(MON_1000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_T4.getMessage(), CustomMaterial.CREATION_MONEY.getData()), howToAdd), howToRemove));
		preset.setItem(MON_10000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_T5.getMessage(), CustomMaterial.CREATION_MONEY.getData()), howToAdd), howToRemove));
		preset.setItem(MON_100000, ItemUtils.addToLore(ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_MAX.getMessage(), CustomMaterial.CREATION_MONEY.getData()), howToAdd), howToRemove));
		
		preset.setItem(MON_CUSTOM,ItemUtils.addToLore(ItemUtils.createItem(CustomMaterial.CREATION_MONEY.getMaterial(), c + Message.CREATION_MONEY_CUSTOM.getMessage(), CustomMaterial.CREATION_MONEY.getData()), Message.CREATION_MONEY_CUSTOM_DESC.getMessage()));
		
		preset.setItem(SIDE_HEADS, ItemUtils.createItem(CustomMaterial.CREATION_SIDE_HEADS.getMaterial(), ChatColor.AQUA + Message.HEADS.getMessage().toUpperCase(), CustomMaterial.CREATION_SIDE_HEADS.getData()));
		preset.setItem(SIDE_TAILS, ItemUtils.createItem(CustomMaterial.CREATION_SIDE_TAILS.getMaterial(), ChatColor.AQUA + Message.TAILS.getMessage().toUpperCase(), CustomMaterial.CREATION_SIDE_TAILS.getData()));
		
		preset.setItem(BACK, ItemUtils.createItem(CustomMaterial.BACK.getMaterial(), Message.ANIMATION_FRAMEEDITOR_BACK.getMessage(), CustomMaterial.BACK.getData()));
	}
	
	private double getMaxMoney(String player){
		double use = ConfigVar.MAX_AMOUNT.getDouble();
		double plMon = CoinFlipper.getEcomony().getBalance(player);
		
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
		
		if (e.getInventory().getHolder() instanceof CreationGUIHolder){
			data.remove(e.getPlayer().getName());
		}
	}
	
	public void openInventory(Player player){
		
		
		if (data.containsKey(player.getName())){
			data.remove(player.getPlayer().getName());
		}
		
		Inventory pInv = Bukkit.createInventory(new CreationGUIHolder(), 45, Message.CREATION_NAME.getMessage());
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
		if (!(e.getInventory().getHolder() instanceof CreationGUIHolder))return;
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
			e.getWhoClicked().sendMessage(Message.CREATION_MONEY_CUSTOM_SPEC.getMessage());
			e.getWhoClicked().closeInventory();
			
			Debug.print("Using PL: " + HookManager.getManager().isHooked(HookType.ProtocolLib));
			Debug.print("Sign input: " + ConfigVar.SIGN_INPUT.getBoolean());
			
			if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean()
					&& CoinFlipper.versionId <= 12){
				Debug.print("sign");
				
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
		
		String side = String.valueOf(data.getSide()).replaceAll("0", Message.TAILS.getMessage().toUpperCase())
				.replaceAll("1", Message.HEADS.getMessage().toUpperCase());
		
		if (data.getSide() == 0){
			inv.setItem(BET_SIDE, ItemUtils.createItem(CustomMaterial.CREATION_SIDE_TAILS.getMaterial(), Message.CREATION_SIDE.getMessage().replaceAll("%SIDE%", side), CustomMaterial.CREATION_SIDE_TAILS.getData()));
		}else{
			inv.setItem(BET_SIDE, ItemUtils.createItem(CustomMaterial.CREATION_SIDE_HEADS.getMaterial(), Message.CREATION_SIDE.getMessage().replaceAll("%SIDE%", side), CustomMaterial.CREATION_SIDE_HEADS.getData()));
		}

		
		ItemUtils.setName(inv.getItem(BET_AMOUNT), Message.CREATION_MONEY.getMessage().replaceAll("%MONEY%", GeneralUtils.getFormattedNumbers(data.getMoney())));
		
		ItemStack headNew = ItemUtils.getSkull(player.getName());
		ItemUtils.setName(headNew, Message.MENU_HEAD_GAME.getMessage().replaceAll("%ID%", ""));
		ItemUtils.addToLore(headNew, Message.MENU_HEAD_PLAYER.getMessage().replaceAll("%PLAYER%", player.getName()));
		ItemUtils.addToLore(headNew, Message.MENU_HEAD_MONEY.getMessage().replaceAll("%MONEY%", GeneralUtils.getFormattedNumbers(data.getMoney())));
		ItemUtils.addToLore(headNew, Message.MENU_HEAD_SIDE.getMessage().replaceAll("%SIDE%", side));
		
		inv.setItem(BET_FINALIZE, headNew);
		
	}
	
	@EventHandler
	public void protocolLibHookInput(SignInputEvent e){
		
		if (!HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean())return;
		
		Player p = e.getPlayer();
		
		if (!customMon.contains(p.getName()) || !data.containsKey(p.getName()))return;
		
		if (e.getLine(0).equalsIgnoreCase("exit")){
			p.openInventory(data.get(p.getName()).getInventory());
			return;
		}
		
		try{
			double mon = Double.parseDouble(e.getLine(0));
			
			if (mon < ConfigVar.MIN_AMOUNT.getDouble()){
				p.sendMessage(Message.CREATION_MONEY_CUSTOM_TOOLITTLE.getMessage());
				HookProtocolLib.getHook().openSignInput(e.getPlayer());
				return;
			}else if (mon > ConfigVar.MAX_AMOUNT.getDouble()){
				p.sendMessage(Message.CREATION_MONEY_CUSTOM_TOOMUCH.getMessage());
				HookProtocolLib.getHook().openSignInput(e.getPlayer());
				return;
			}
			if (mon > CoinFlipper.getEcomony().getBalance(e.getPlayer())){
				p.sendMessage(Message.CREATION_MONEY_CUSTOM_NOMONEY.getMessage());
				HookProtocolLib.getHook().openSignInput(e.getPlayer());
				return;
			}
			
			data.get(p.getName()).setMoney(mon);
			
			
			
			p.sendMessage(Message.CREATION_MONEY_CUSTOM_SUCCESS.getMessage().replaceAll("%MONEY%", mon+""));
			
			p.openInventory(data.get(p.getName()).getInventory());
			
			this.refreshInventory(p);
			customMon.remove(e.getPlayer().getName());
		}catch(Exception ex){
			p.sendMessage(Message.INPUT_NOTNUM.getMessage());
			HookProtocolLib.getHook().openSignInput(e.getPlayer());
		}
	}
	
	@EventHandler
	public void customValue(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		
		
		
		if (!customMon.contains(p.getName()) || !data.containsKey(p.getName()))return;
		e.setCancelled(true);
		
		if (e.getMessage().equalsIgnoreCase("exit")){
			p.openInventory(data.get(p.getName()).getInventory());
			return;
		}
		
		try{
			double mon = Double.parseDouble(e.getMessage());
			
			if (mon < ConfigVar.MIN_AMOUNT.getDouble()){
				p.sendMessage(Message.CREATION_MONEY_CUSTOM_TOOLITTLE.getMessage());
				return;
			}else if (mon > ConfigVar.MAX_AMOUNT.getDouble()){
				p.sendMessage(Message.CREATION_MONEY_CUSTOM_TOOMUCH.getMessage());
				return;
			}
			if (mon > CoinFlipper.getEcomony().getBalance(e.getPlayer())){
				p.sendMessage(Message.CREATION_MONEY_CUSTOM_NOMONEY.getMessage());
				return;
			}
			
			data.get(p.getName()).setMoney(mon);
			
			
			
			p.sendMessage(Message.CREATION_MONEY_CUSTOM_SUCCESS.getMessage().replaceAll("%MONEY%", mon+""));
			
			p.openInventory(data.get(p.getName()).getInventory());
			
			this.refreshInventory(p);
			customMon.remove(e.getPlayer().getName());
		}catch(Exception ex){
			p.sendMessage(Message.INPUT_NOTNUM.getMessage());
		}
	}
}

class CreationGUIHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
