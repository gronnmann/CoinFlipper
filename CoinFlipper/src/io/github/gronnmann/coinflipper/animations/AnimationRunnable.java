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
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;

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
			p1.openInventory(animation.getFrame(phase));
		}
		if (p2 != null){
			p2.openInventory(animation.getFrame(phase));
		}
		
		
		
		//Sound win
		if (phase == 30){
				try{
					p1.playSound(p1.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_winner_chosen").toUpperCase()), 1F, 1F);
				}catch(Exception e){}
				try{
					p2.playSound(p2.getLocation(), Sound.valueOf(ConfigManager.getManager().getConfig().getString("sound_winner_chosen").toUpperCase()) , 1F, 1F);
				}catch(Exception e){}
				Player win = Bukkit.getPlayer(winner);
				if (win != null){
						win.sendMessage(MessagesManager.getMessage(Message.BET_WON).replaceAll("%MONEY%", winMoney+""));
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
	}
}
