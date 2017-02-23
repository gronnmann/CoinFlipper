package io.github.gronnmann.coinflipper;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.bets.Bet;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import net.milkbowl.vault.economy.EconomyResponse;

public class GUI implements Listener{
	private GUI(){}
	private static GUI instance = new GUI();
	public static GUI getInstance(){
		return instance;
	}
	
	private Plugin pl;
	private Inventory selectionScreen;
	private ArrayList<String> removers = new ArrayList<String>();
	public void setup(Plugin pl){
		this.pl = pl;
		selectionScreen = Bukkit.createInventory(null, 54, "CoinFlipper Selection");
		
	}
	
	public void refreshGameManager(){
		int amo = 0;
		selectionScreen.clear();
		for (Bet b : BettingManager.getManager().getBets()){
			if (amo > 44)return;
			selectionScreen.setItem(amo, getSkull(b));
			amo++;
		}
		ItemStack purp = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 10);
		for (int i = 45; i <= 53; i++){
			selectionScreen.setItem(i, purp);
		}
		ItemStack help = new ItemStack(Material.BOOK);
		ItemMeta helpM = help.getItemMeta();
		helpM.setDisplayName(ChatColor.BOLD + MessagesManager.getMessage(Message.HELP_ITEM_L1));
		ArrayList<String> lores = new ArrayList<String>();
		lores.add(MessagesManager.getMessage(Message.HELP_ITEM_L2));
		lores.add(MessagesManager.getMessage(Message.HELP_ITEM_L3));
		lores.add(MessagesManager.getMessage(Message.HELP_ITEM_L4));
		helpM.setLore(lores);
		help.setItemMeta(helpM);
		selectionScreen.setItem(49, help);
	}
	
	public void openGameManager(Player p){
		this.refreshGameManager();
		p.openInventory(selectionScreen);
	}
	
	private void generateAnimations(String p1, String p2, String winner){
		
		ItemStack sk1 = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
		ItemStack sk2 = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
		SkullMeta sm1 = (SkullMeta) sk1.getItemMeta();
		SkullMeta sm2 = (SkullMeta) sk2.getItemMeta();
		sm1.setOwner(p1);sk1.setItemMeta(sm1);
		sm2.setOwner(p2);sk2.setItemMeta(sm2);
		
		String invName = "CoinFlipper: " + p1 + " vs. " + p2;
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		int vID = Integer.parseInt(packageName.split("_")[1]);
		
		if (invName.length() > 32 && vID < 9){
			invName = "CoinFlipper Game";
		}
		
		
		Inventory inv = Bukkit.createInventory(null, 45, invName);
		
		ItemStack gpG = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5);
		for (int i = 0;i <= 44; i++){
			inv.setItem(i, gpG);
		}
		
		try{
			Bukkit.getPlayer(p1).openInventory(inv);
			
		}catch(Exception e){}
		
		try{
			Bukkit.getPlayer(p2).openInventory(inv);
			
		}catch(Exception e){}
		
		final Animation animation = new Animation(p1, p2, inv, winner, sk1, sk2);
		animation.runTaskTimer(pl, 5, 2);
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
		lore.add(MessagesManager.getMessage(Message.MENU_HEAD_PLAYER).replaceAll("%PLAYER%", b.getPlayer()));
		lore.add(MessagesManager.getMessage(Message.MENU_HEAD_MONEY).replaceAll("%MONEY%", b.getAmount()+""));
		int hours = b.getTimeRemaining()/60;
		int mins = b.getTimeRemaining()-hours*60;
		lore.add(MessagesManager.getMessage(Message.MENU_HEAD_TIMEREMAINING).replaceAll("%HOURS%", hours+"").replaceAll("%MINUTES%", mins+""));
		String side = ".";
		if (b.getSide() == 0){
			side = MessagesManager.getMessage(Message.TAILS);
		}else{
			side = MessagesManager.getMessage(Message.HEADS);
		}
		lore.add(MessagesManager.getMessage(Message.MENU_HEAD_SIDE).replaceAll("%SIDE%", side));
		sm.setLore(lore);
		sm.setDisplayName(MessagesManager.getMessage(Message.MENU_HEAD_GAME).replaceAll("%ID%", b.getID()+""));
		skull.setItemMeta(sm);
		return skull;
	}
	
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		if (!e.getInventory().getName().contains("CoinFlipper"))return;
		e.setCancelled(true);
		if (e.getCurrentItem() == null)return;
		if (e.getCurrentItem().getItemMeta()==null)return;
		if (e.getInventory().getName().contains("Game"))return;
		if (e.getSlot() == 48){
			
		}
		if (e.getSlot() == 50){
			
		}
		
		if (!(e.getCurrentItem().getType().equals(Material.SKULL_ITEM)))return;
		
		ItemStack item = e.getCurrentItem();
		String ID = ChatColor.stripColor(item.getItemMeta().getDisplayName().split(" ")[1]);
		ID = ID.replaceAll("#", "");	
		int id = Integer.parseInt(ID);
		
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
					p.sendMessage(MessagesManager.getMessage(Message.BET_REMOVE_SELF_SUCCESSFUL));
					Main.getEcomony().depositPlayer(p.getName(), b.getAmount());
				}else{
					p.sendMessage(MessagesManager.getMessage(Message.BET_REMOVE_SELF_CONFIRM));
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
					p.sendMessage(MessagesManager.getMessage(Message.BET_REMOVE_OTHER_SUCCESSFUL).replaceAll("%PLAYER%", b.getPlayer()));
					Player bP = Bukkit.getPlayer(b.getPlayer());
					if (bP != null){
						bP.sendMessage(MessagesManager.getMessage(Message.BET_REMOVE_OTHER_NOTIFICATION));
					}
					
					Main.getEcomony().depositPlayer(b.getPlayer(), b.getAmount());
				}else{
					p.sendMessage(MessagesManager.getMessage(Message.BET_REMOVE_OTHER_CONFIRM).replaceAll("%PLAYER%", b.getPlayer()));
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
			p.sendMessage(MessagesManager.getMessage(Message.BET_CHALLENGE_CANTSELF));
			return;
		}
		
		//Check if player can afford
		EconomyResponse response = Main.getEcomony().withdrawPlayer(p, b.getAmount());
		if (!response.transactionSuccess()){
			p.sendMessage(MessagesManager.getMessage(Message.BET_CHALLENGE_NOMONEY));
			return;
		}
		
		final double winAmount = b.getAmount()*2;
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
		//Money stats end
		
		
		//Create animations & give money
		Main.getEcomony().depositPlayer(Bukkit.getOfflinePlayer(winner), winAmount);
		this.generateAnimations(p.getName(), b.getPlayer(), winner);
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(pl, new Runnable(){
			public void run(){
				Player winnerP = Bukkit.getPlayer(winner);
			if (winnerP != null){
				winnerP.sendMessage(MessagesManager.getMessage(Message.BET_WON).replaceAll("%MONEY%", winAmount+""));
			}
			}
		}, 60);
		
		BettingManager.getManager().removeBet(b);
		this.refreshGameManager();

	}
	
}
class Animation extends BukkitRunnable{
	String s1, s2, winner;
	Inventory inv;
	ItemStack sk1, sk2;
	private int phase;
	private boolean hed;
	public Animation(String s1, String s2, Inventory inv, String winner, ItemStack sk1, ItemStack sk2){
		this.s1 = s1;
		this.s2 = s2;
		this.winner = winner;
		this.inv = inv;
		this.sk1 = sk1;
		this.sk2 = sk2;
		phase = 0;
		hed = false;
		
	}
	
	ItemStack gpG = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)5);
	ItemStack gpD = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)13);
	
	
	
	
	
	
	
	public void run(){
		
		
		
		inv.clear();
		phase++;
		
		if (phase >= 30){
			if (phase == 30){
				try{
					Player p = Bukkit.getPlayer(s1);
					p.playSound(p.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_winner_chosen").toUpperCase()), 1F, 1F);
				}catch(Exception e){}
				try{
					Player p2 = Bukkit.getPlayer(s2);
					p2.playSound(p2.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_winner_chosen").toUpperCase()) , 1F, 1F);
				}catch(Exception e){}
			}
			ItemStack win = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)4);
			ItemStack win2 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte)14);
			for (int i = 0;i <= 44; i++){
				inv.setItem(i, win);
			}
			
			ItemStack sk1 = new ItemStack(Material.SKULL_ITEM, 1, (byte)3);
			SkullMeta sm1 = (SkullMeta) sk1.getItemMeta();
			sm1.setOwner(winner);
			sm1.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "WINNER: " + ChatColor.AQUA +winner);
			sk1.setItemMeta(sm1);
			
			inv.setItem(22, sk1);
			
			//Winner Firework
			if (phase > 32){
				inv.setItem(13, win2);
				inv.setItem(21, win2);
				inv.setItem(23, win2);
				inv.setItem(31, win2);
			}
			if (phase > 34){
				inv.setItem(4, win2);
				inv.setItem(12, win2);
				inv.setItem(14, win2);
				inv.setItem(20, win2);
				inv.setItem(24, win2);
				inv.setItem(30, win2);
				inv.setItem(32, win2);
				inv.setItem(40, win2);
			}
			if (phase > 36){
				inv.setItem(3, win2);
				inv.setItem(5, win2);
				inv.setItem(11, win2);
				inv.setItem(15, win2);
				inv.setItem(19, win2);
				inv.setItem(25, win2);
				inv.setItem(29, win2);
				inv.setItem(33, win2);
				inv.setItem(39, win2);
				inv.setItem(41, win2);
			}
			if (phase > 38){
				inv.setItem(2, win2);
				inv.setItem(6, win2);
				inv.setItem(10, win2);
				inv.setItem(16, win2);
				inv.setItem(18, win2);
				inv.setItem(26, win2);
				inv.setItem(28, win2);
				inv.setItem(34, win2);
				inv.setItem(38, win2);
				inv.setItem(42, win2);
			}
			if (phase > 40){
				inv.setItem(1, win2);
				inv.setItem(7, win2);
				inv.setItem(9, win2);
				inv.setItem(17, win2);
				inv.setItem(27, win2);
				inv.setItem(35, win2);
				inv.setItem(37, win2);
				inv.setItem(43, win2);
			}
			if (phase > 42){
				inv.setItem(0, win2);
				inv.setItem(8, win2);
				inv.setItem(36, win2);
				inv.setItem(44, win2);
			}
			
			
			return;
			
			
		}
		
		for (int i = 0;i <= 44; i++){
			inv.setItem(i, gpG);
		}
		
		//Wave
		if (phase == 0 || phase == 5 ||phase==10||phase==15||phase==20||phase==25){
			for (int i = 0; i<=8;i++){
				inv.setItem(i, gpD);
			}
		}else if (phase == 1 || phase == 6 || phase == 11 || phase == 16||phase==21||phase==26){
			for (int i = 9; i<=17;i++){
				inv.setItem(i, gpD);
			}
		}else if (phase == 2 || phase == 7||phase==12||phase==17||phase==22||phase==27){
			for (int i = 18; i<=26;i++){
				inv.setItem(i, gpD);
			}
		}else if (phase == 3 || phase == 8||phase==13||phase==18||phase==23||phase==28){
			for (int i = 27; i<=35;i++){
				inv.setItem(i, gpD);
			}
		}else {
			for (int i = 36; i<=44;i++){
				inv.setItem(i, gpD);
			}
		}
		
		
		//Heads
		if (hed){
			inv.setItem(22, sk1);
			
			try{
				Bukkit.getPlayer(s1).closeInventory();
				Bukkit.getPlayer(s1).openInventory(inv);
			}catch(Exception e){}
			try{
				Bukkit.getPlayer(s2).closeInventory();
				Bukkit.getPlayer(s2).openInventory(inv);
			}catch(Exception e){}
		}else{
			inv.setItem(22, sk2);
			
			try{
				Bukkit.getPlayer(s1).closeInventory();
				Bukkit.getPlayer(s1).openInventory(inv);
			}catch(Exception e){}
			try{
				Bukkit.getPlayer(s2).closeInventory();
				Bukkit.getPlayer(s2).openInventory(inv);
			}catch(Exception e){}
		}
		
		/*
		
		if (phase == 0||phase == 9 || phase == 19){
			hed=false;
		}else if (phase == 4 || phase == 14 || phase == 24){
			hed=true;
		}
		*/
		hed = !hed;
		
		
		
		try{
			Player p = Bukkit.getPlayer(s1);
			p.playSound(p.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_while_choosing").toUpperCase()) , 1F, 1F);
		}catch(Exception e){}
		try{
			Player p2 = Bukkit.getPlayer(s2);
			p2.playSound(p2.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_while_choosing").toUpperCase()) , 1F, 1F);
		}catch(Exception e){}
		
	}
}

