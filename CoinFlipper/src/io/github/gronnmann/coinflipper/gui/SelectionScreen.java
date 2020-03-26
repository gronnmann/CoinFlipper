package io.github.gronnmann.coinflipper.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.animations.AnimationRunnable;
import io.github.gronnmann.coinflipper.animations.AnimationsManager;
import io.github.gronnmann.coinflipper.bets.Bet;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.CustomMaterial;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.events.BetChallengeEvent;
import io.github.gronnmann.coinflipper.events.BetPlayEvent;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import net.milkbowl.vault.economy.EconomyResponse;

public class SelectionScreen implements Listener{
	private SelectionScreen(){}
	private static SelectionScreen instance = new SelectionScreen();
	public static SelectionScreen getInstance(){
		return instance;
	}
	
	private Plugin pl;
	private Inventory selectionScreen;
	private ArrayList<String> removers = new ArrayList<String>();
	
	private int CREATE = 46;
	
	public void setup(Plugin pl){
		this.pl = pl;
		selectionScreen = Bukkit.createInventory(new SelectionScreenHolder(), 54, Message.GUI_SELECTION.getMessage());
		
	}
	
	public void refreshGameManager(){
		int amo = 0;
		selectionScreen.clear();
		for (Bet b : BettingManager.getManager().getBets()){
			if (amo > 44)return;
			selectionScreen.setItem(amo, getSkull(b));
			amo++;
		}
		for (int i = 45; i <= 53; i++){
			selectionScreen.setItem(i, ItemUtils.createItem(CustomMaterial.SELECTION_FILLING.getMaterial(), " ", CustomMaterial.SELECTION_FILLING.getData()));
		}
		ItemStack help = new ItemStack(CustomMaterial.SELECTION_SYNTAX.getMaterial(), 1, (short)CustomMaterial.SELECTION_SYNTAX.getData());
		ItemMeta helpM = help.getItemMeta();
		helpM.setDisplayName(ChatColor.BOLD + Message.HELP_ITEM_L1.getMessage());
		ArrayList<String> lores = new ArrayList<String>();
		lores.add(Message.HELP_ITEM_L2.getMessage());
		lores.add(Message.HELP_ITEM_L3.getMessage());
		lores.add(Message.HELP_ITEM_L4.getMessage());
		helpM.setLore(lores);
		help.setItemMeta(helpM);
		selectionScreen.setItem(49, help);
		
		selectionScreen.setItem(CREATE, ItemUtils.createItem(CustomMaterial.SELECTION_CREATE.getMaterial(), Message.CREATE.getMessage(), 
				CustomMaterial.SELECTION_CREATE.getData()));
	}
	
	public void openGameManager(Player p){
		this.refreshGameManager();
		p.openInventory(selectionScreen);
	}
	
	private void generateAnimations(String p1, String p2, String winner, double moneyWon, String anim){
		
		String invName = Message.GUI_GAME.getMessage().replaceAll("%PLAYER1%", p1).replaceAll("%PLAYER2%", p2);
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		int vID = Integer.parseInt(packageName.split("_")[1]);
		
		if (invName.length() > 32 && vID < 9){
			invName = Message.GUI_GAME_18.getMessage();
		}
		
		
		final AnimationRunnable animation = new AnimationRunnable(p1, p2, winner, moneyWon, anim,
				invName);
		animation.runTaskTimer(pl, 0, 2);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable(){
			public void run(){
				animation.cancel();
			}
		}, 100);
		//22 is middle
		
		
		
		
		
		
	}
	
	
	
	private ItemStack getSkull(Bet b){
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
		SkullMeta sm = (SkullMeta)skull.getItemMeta();
		sm.setOwner(b.getPlayer());
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(Message.MENU_HEAD_PLAYER.getMessage().replaceAll("%PLAYER%", b.getPlayer()));
		lore.add(Message.MENU_HEAD_MONEY.getMessage().replaceAll("%MONEY%", GeneralUtils.getFormattedNumbers(b.getAmount())));
		int hours = b.getTimeRemaining()/60;
		int mins = b.getTimeRemaining()-hours*60;
		lore.add(Message.MENU_HEAD_TIMEREMAINING.getMessage().replaceAll("%HOURS%", hours+"").replaceAll("%MINUTES%", mins+""));
		String side = ".";
		if (b.getSide() == 0){
			side = Message.TAILS.getMessage();
		}else{
			side = Message.HEADS.getMessage();
		}
		lore.add(Message.MENU_HEAD_SIDE.getMessage().replaceAll("%SIDE%", side));
		sm.setLore(lore);
		sm.setDisplayName(Message.MENU_HEAD_GAME.getMessage().replaceAll("%ID%", b.getID()+""));
		skull.setItemMeta(sm);
		return skull;
	}
	
	
	@EventHandler
	public void gameAntiClicker(InventoryClickEvent e){
		if (e.getInventory().getHolder() instanceof GameInventoryHolder){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void detectClicks(InventoryClickEvent e){
		if (!(e.getInventory().getHolder() instanceof SelectionScreenHolder))return;
		e.setCancelled(true);
		if (e.getCurrentItem() == null)return;
		if (e.getCurrentItem().getItemMeta()==null)return;
		if (e.getSlot() == CREATE){
			
			if (CoinFlipper.getEcomony().getBalance(e.getWhoClicked().getName())
					< ConfigVar.MIN_AMOUNT.getDouble()){
				e.getWhoClicked().sendMessage(Message.PLACE_NOT_POSSIBLE_NOMONEY.getMessage().replaceAll("%MINMON%", ConfigVar.MIN_AMOUNT.getDouble()+""));
				return;
			}
			CreationGUI.getInstance().openInventory((Player) e.getWhoClicked());
		}
		
		
		if (!(e.getCurrentItem().getType().equals(Material.SKULL_ITEM)))return;
		
		ItemStack item = e.getCurrentItem();
		String ID = ChatColor.stripColor(item.getItemMeta().getDisplayName());
		ID = ID.replaceAll("#", "");
		int id = GeneralUtils.getIntInString(ID);
		
		Player p = (Player) e.getWhoClicked();
		Bet b = BettingManager.getManager().getBet(id);
		
		
		//Removal of bet
		if (e.isRightClick() 	){
			//Own remove
			if (p.getName().equals(b.getPlayer())){
				if (!p.hasPermission("coinflipper.remove.self"))return;
				if (removers.contains(p.getName())){
					BettingManager.getManager().removeBet(b);
					this.refreshGameManager();
					p.sendMessage(Message.BET_REMOVE_SELF_SUCCESSFUL.getMessage());
					CoinFlipper.getEcomony().depositPlayer(p.getName(), b.getAmount());
				}else{
					p.sendMessage(Message.BET_REMOVE_SELF_CONFIRM.getMessage());
					removers.add(p.getName());
					final String pN = p.getName();
					Bukkit.getScheduler().scheduleAsyncDelayedTask(pl, new Runnable() {
						
						public void run() {
							if (removers.contains(pN)){
								removers.remove(pN);
							}
						}
					}, 200);
				}
				
				
				
				
				return;
			}
			//Other player
			else{
				if (!p.hasPermission("coinflipper.remove.other"))return;
				if (removers.contains(p.getName())){
					BettingManager.getManager().removeBet(b);
					this.refreshGameManager();
					p.sendMessage(Message.BET_REMOVE_OTHER_SUCCESSFUL.getMessage().replaceAll("%PLAYER%", b.getPlayer()));
					Player bP = Bukkit.getPlayer(b.getPlayer());
					if (bP != null){
						bP.sendMessage(Message.BET_REMOVE_OTHER_NOTIFICATION.getMessage());
					}
					
					CoinFlipper.getEcomony().depositPlayer(b.getPlayer(), b.getAmount());
				}else{
					p.sendMessage(Message.BET_REMOVE_OTHER_CONFIRM.getMessage().replaceAll("%PLAYER%", b.getPlayer()));
					removers.add(p.getName());
					final String pN = p.getName();
					Bukkit.getScheduler().scheduleAsyncDelayedTask(pl, new Runnable() {
						
						public void run() {
							if (removers.contains(pN)){
								removers.remove(pN);
							}
						}
					}, 200);
				}
			}
			
			
			return;
		}
		
		
		//Challenging
		//Check if player challenges himself
		if (p.getName().equals(b.getPlayer())){
			p.sendMessage(Message.BET_CHALLENGE_CANTSELF.getMessage());
			return;
		}
		
		if (GamesManager.getManager().isSpinning(b.getPlayer())){
			p.sendMessage(Message.BET_CHALLENGE_ALREADYSPINNING.getMessage());
			return;
		}
		
		//Check if player can afford
		EconomyResponse response = CoinFlipper.getEcomony().withdrawPlayer(p, b.getAmount());
		if (!response.transactionSuccess()){
			p.sendMessage(Message.BET_CHALLENGE_NOMONEY.getMessage());
			return;
		}
		
		BetChallengeEvent chEvent = new BetChallengeEvent(p, b);
		Bukkit.getPluginManager().callEvent(chEvent);
		
		if (chEvent.isCancelled())return;
		
		double winAmount = b.getAmount()*2;
		final double tax = ConfigVar.TAX_PERCENTAGE.getDouble();
		winAmount = (100-tax)*winAmount/100;
		
		final String winner = BettingManager.getManager().challengeBet(b, p);
		
		
		//Add money stats
		OfflinePlayer p1 = Bukkit.getOfflinePlayer(p.getName());
		OfflinePlayer p2 = Bukkit.getOfflinePlayer(b.getPlayer());
		
		StatsManager.getManager().getStats(p1.getUniqueId().toString()).addMoneySpent(b.getAmount());
		StatsManager.getManager().getStats(p2.getUniqueId().toString()).addMoneySpent(b.getAmount());
		
		if (winner.equals(p1.getName())){
			StatsManager.getManager().getStats(p1.getUniqueId().toString()).addMoneyWon(winAmount);
		}else{
			StatsManager.getManager().getStats(p2.getUniqueId().toString()).addMoneyWon(winAmount);
		}
		
		//Call event
		BetPlayEvent bpe = new BetPlayEvent(p.getName(), b.getPlayer(), winner, b.getAnimation(), winAmount, b);
		Bukkit.getPluginManager().callEvent(bpe);
		
		
		
		if (b.getAnimation() == null){
			b.setAnimation(AnimationsManager.getManager().getDefault());
		}
		
		//Create animations
		
		Debug.print(p.getName());
		Debug.print(b.getPlayer());
		Debug.print(b.getAnimation().getName());
		
		GamesManager.getManager().setSpinning(p.getName(), true);
		GamesManager.getManager().setSpinning(b.getPlayer(), true);
		
		this.generateAnimations(p.getName(), b.getPlayer(), winner, winAmount, b.getAnimation().getName());
		
		BettingManager.getManager().removeBet(b);
		this.refreshGameManager();

	}
	
}

class SelectionScreenHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
