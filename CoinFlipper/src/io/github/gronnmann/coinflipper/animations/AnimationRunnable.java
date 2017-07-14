package io.github.gronnmann.coinflipper.animations;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.hook.HookChatPerWorld;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.PacketUtils;
import io.github.gronnmann.utils.coinflipper.PacketUtils.TitleType;

public class AnimationRunnable extends BukkitRunnable{
	private String s1, s2, winner, winMoneyFormatted;
	private int phase;
	private double winMoney;
	

	PersonalizedAnimation animation;
	
	
	public AnimationRunnable(String s1, String s2, String winner, double winMoney, String animationS, String inventoryName){
		this.s1 = s1;
		this.s2 = s2;
		this.winner = winner;
		this.phase = 0;
		this.winMoney = winMoney;
		this.winMoneyFormatted = GeneralUtils.getFormattedNumbers(winMoney);
		
				
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
					
					PacketUtils.sendTitle(p1, MessagesManager.getMessage(Message.BET_TITLE_COMBAT), 
							TitleType.TITLE, 20, 40, 20);
					PacketUtils.sendTitle(p1, MessagesManager.getMessage(Message.BET_START_COMBAT), 
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
					
					PacketUtils.sendTitle(p2, MessagesManager.getMessage(Message.BET_TITLE_COMBAT), 
							TitleType.TITLE, 20, 40, 20);
					PacketUtils.sendTitle(p2, MessagesManager.getMessage(Message.BET_START_COMBAT), 
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
					
					String winMsg = MessagesManager.getMessage(Message.BET_WON).replaceAll("%MONEY%", winMoneyFormatted+"").replaceAll("%WINNER%",
							winner).replaceAll("%LOSER%", loser);
					win.sendMessage(winMsg);
					
					PacketUtils.sendTitle(win, MessagesManager.getMessage(Message.BET_TITLE_VICTORY), TitleType.TITLE, 20, 60, 20);
					PacketUtils.sendTitle(win, winMsg, TitleType.SUBTITLE, 20, 60, 20);
				}
				Player los = Bukkit.getPlayer(loser);
				if (los != null){
					String losMsg = MessagesManager.getMessage(Message.BET_LOST).replaceAll("%MONEY%", winMoneyFormatted+"").replaceAll("%WINNER%",
							winner).replaceAll("%LOSER%", loser);
					los.sendMessage(losMsg);
					
					PacketUtils.sendTitle(los, MessagesManager.getMessage(Message.BET_TITLE_LOSS), TitleType.TITLE, 20, 60, 20);
					PacketUtils.sendTitle(los, losMsg, TitleType.SUBTITLE, 20, 60, 20);
				}
				
				if ( !(ConfigManager.getManager().getConfig().getString("value_needed_to_broadcast") == null) && 
						winMoney >= ConfigManager.getManager().getConfig().getDouble("value_needed_to_broadcast") && 
						ConfigManager.getManager().getConfig().getDouble("value_needed_to_broadcast") != 0){
					
					if (HookManager.getManager().isHooked(HookType.ChatPerWorld)){
						if (Bukkit.getPlayer(winner) == null){
							return;
						}
						Debug.print("ChatPerWorld broadcast.");
						for (Player oPl : Bukkit.getOnlinePlayers()){
							Debug.print("Testing player " + oPl.getName());
							if (HookChatPerWorld.getHook().getReceivers(Bukkit.getPlayer(winner)).contains(oPl)){
								Debug.print(oPl.getName() + " approved.");
								oPl.sendMessage(MessagesManager.getMessage(Message.HIGH_GAME_BROADCAST)
										.replaceAll("%MONEY%", winMoneyFormatted+"").replaceAll("%WINNER%",
									winner).replaceAll("%LOSER%", loser));
							}
						}
					}else{
						Debug.print("Normal broadcast.");
						Bukkit.broadcastMessage(MessagesManager.getMessage(Message.HIGH_GAME_BROADCAST)
							.replaceAll("%MONEY%", winMoneyFormatted+"").replaceAll("%WINNER%",
									winner).replaceAll("%LOSER%", loser));
					}
					
					
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
