package io.github.gronnmann.coinflipper.animations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;

public class AnimationRunnableOld extends BukkitRunnable{
	String s1, s2, winner;
	Inventory inv;
	ItemStack sk1, sk2;
	private int phase;
	private boolean hed;
	private double winMoney;
	
	public AnimationRunnableOld(String s1, String s2, Inventory inv, String winner, ItemStack sk1, ItemStack sk2, double winMoney){
		this.s1 = s1;
		this.s2 = s2;
		this.winner = winner;
		this.inv = inv;
		this.sk1 = sk1;
		this.sk2 = sk2;
		phase = 0;
		hed = false;
		this.winMoney = winMoney;
		
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
				Player win = Bukkit.getPlayer(winner);
				if (win != null){
					win.sendMessage(MessagesManager.getMessage(Message.BET_WON).replaceAll("%MONEY%", winMoney+""));
				}
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
