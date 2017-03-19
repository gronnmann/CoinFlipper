package io.github.gronnmann.coinflipper.animations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.utils.ReflectionUtils;
import io.github.gronnmann.utils.ReflectionUtils.TitleType;

public class AnimationRunnable extends BukkitRunnable{
	String s1, s2, winner;
	private int phase;
	private double winMoney;
	

	PersonalizedAnimation animation;
	
	
	public AnimationRunnable(String s1, String s2, String winner, double winMoney, String animationS, String inventoryName){
		this.s1 = s1;
		this.s2 = s2;
		this.winner = winner;
		phase = 0;
		this.winMoney = winMoney;
		
				
		Animation anim = AnimationsManager.getManager().getAnimation(animationS);
		
		animation = new PersonalizedAnimation(anim, winner, s1, s2, inventoryName);
	}
	
	
	public void run(){
		
		
		
		phase++;
		
		
		Player p1 = Bukkit.getPlayer(s1);
		Player p2 = Bukkit.getPlayer(s2);
		
		if (p1 != null){
			if (!HookManager.getManager().isTagged(p1)){
				p1.openInventory(animation.getFrame(phase));
			}else{
				if (phase == 1){
					p1.sendMessage(MessagesManager.getMessage(Message.BET_START_COMBAT));
					
					ReflectionUtils.sendTitle(p1, MessagesManager.getMessage(Message.BET_TITLE_COMBAT), 
							TitleType.TITLE, 20, 40, 20);
					ReflectionUtils.sendTitle(p1, MessagesManager.getMessage(Message.BET_START_COMBAT), 
							TitleType.SUBTITLE, 20, 40, 20);
				}
			}
		}
		if (p2 != null){
			if (!HookManager.getManager().isTagged(p1)){
				p2.openInventory(animation.getFrame(phase));
			}
			else{
				if (phase == 1){
					p2.sendMessage(MessagesManager.getMessage(Message.BET_START_COMBAT));
					
					ReflectionUtils.sendTitle(p2, MessagesManager.getMessage(Message.BET_TITLE_COMBAT), 
							TitleType.TITLE, 20, 40, 20);
					ReflectionUtils.sendTitle(p2, MessagesManager.getMessage(Message.BET_START_COMBAT), 
							TitleType.SUBTITLE, 20, 40, 20);
				}
			}
		}
		
		
		
		//Sound win
		if (phase == 30){
				try{
					p1.playSound(p1.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_winner_chosen").toUpperCase()), 1F, 1F);
				}catch(Exception e){}
				try{
					p2.playSound(p2.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_winner_chosen").toUpperCase()) , 1F, 1F);
				}catch(Exception e){}
				
				String loser = s1;
				if (s1.equals(winner)){
					loser = s2;
				}
				
				
				Player win = Bukkit.getPlayer(winner);
				if (win != null){
					
					String winMsg = MessagesManager.getMessage(Message.BET_WON).replaceAll("%MONEY%", winMoney+"").replaceAll("%WINNER%",
							winner).replaceAll("%LOSER%", loser);
					win.sendMessage(winMsg);
					
					ReflectionUtils.sendTitle(win, MessagesManager.getMessage(Message.BET_TITLE_VICTORY), TitleType.TITLE, 20, 60, 20);
					ReflectionUtils.sendTitle(win, winMsg, TitleType.SUBTITLE, 20, 60, 20);
				}
				Player los = Bukkit.getPlayer(loser);
				if (los != null){
					String losMsg = MessagesManager.getMessage(Message.BET_LOST).replaceAll("%MONEY%", winMoney+"").replaceAll("%WINNER%",
							winner).replaceAll("%LOSER%", loser);
					los.sendMessage(losMsg);
					
					ReflectionUtils.sendTitle(los, MessagesManager.getMessage(Message.BET_TITLE_LOSS), TitleType.TITLE, 20, 60, 20);
					ReflectionUtils.sendTitle(los, losMsg, TitleType.SUBTITLE, 20, 60, 20);
				}
		}
		
		
		//Sound click
		if (phase < 30){
			try{
				p1.playSound(p1.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_while_choosing").toUpperCase()) , 1F, 1F);
			}catch(Exception e){}
			try{
				p2.playSound(p2.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_while_choosing").toUpperCase()) , 1F, 1F);
			}catch(Exception e){}
		}
		
		if (phase == 50){
			
			GamesManager.getManager().setSpinning(s1, false);
			GamesManager.getManager().setSpinning(s2, false);
			
			if (p1 != null && ConfigManager.getManager().getConfig().getBoolean("gui_auto_close")){
				p1.closeInventory();
			}
			if (p2 != null && ConfigManager.getManager().getConfig().getBoolean("gui_auto_close")){
				p2.closeInventory();
			}
		}
	}
}
