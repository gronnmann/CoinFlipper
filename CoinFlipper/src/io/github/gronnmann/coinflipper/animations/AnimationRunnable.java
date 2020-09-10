package io.github.gronnmann.coinflipper.animations;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.hook.HookChatPerWorld;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.PacketUtils;
import io.github.gronnmann.utils.coinflipper.PacketUtils.TitleType;

public class AnimationRunnable extends BukkitRunnable{
	private String s1, s2, winner, winMoneyFormatted;
	private int phase, winFrame;
	private double winMoney;
	

	PersonalizedAnimation animation;
	
	
	public AnimationRunnable(String s1, String s2, String winner, double winMoney, String animationS, String inventoryName){
		this.s1 = s1;
		this.s2 = s2;
		this.winner = winner;
		this.phase = 0;
		this.winMoney = winMoney;
		this.winMoneyFormatted = GeneralUtils.getFormattedNumbers(winMoney);
		
		this.winFrame = ConfigVar.ANIMATIONS_ENABLED.getBoolean() ? ConfigVar.FRAME_WINNER_CHOSEN.getInt() : 1;
				
		Animation anim = AnimationsManager.getManager().getAnimation(animationS);
		
		animation = new PersonalizedAnimation(anim, winner, s1, s2, inventoryName);
	}
	
	
	public void run(){
		
		phase++;
		
		
		Player p1 = Bukkit.getPlayer(s1);
		Player p2 = Bukkit.getPlayer(s2);
		
		if (p1 != null && ConfigVar.ANIMATIONS_ENABLED.getBoolean()){
			if (!HookManager.getManager().isTagged(p1)){
				p1.openInventory(animation.getFrame(phase));
			}else{
				if (phase == 1){
					p1.sendMessage(Message.BET_START_COMBAT.getMessage());
					
					PacketUtils.sendTitle(p1, Message.BET_TITLE_COMBAT.getMessage(), 
							TitleType.TITLE, 20, 40, 20);
					PacketUtils.sendTitle(p1, Message.BET_START_COMBAT.getMessage(), 
							TitleType.SUBTITLE, 20, 40, 20);
				}
			}
		}
		if (!ConfigVar.ANIMATION_ONLY_CHALLENGER.getBoolean()) {
			if (p2 != null && ConfigVar.ANIMATIONS_ENABLED.getBoolean()){
				if (!HookManager.getManager().isTagged(p1)){
					p2.openInventory(animation.getFrame(phase));
				}
				else{
					if (phase == 1){
						p2.sendMessage(Message.BET_START_COMBAT.getMessage());
						
						PacketUtils.sendTitle(p2, Message.BET_TITLE_COMBAT.getMessage(), 
								TitleType.TITLE, 20, 40, 20);
						PacketUtils.sendTitle(p2, Message.BET_START_COMBAT.getMessage(), 
								TitleType.SUBTITLE, 20, 40, 20);
					}
				}
			}
		}
		
		
		
		if (phase == winFrame){
			
			//Give money
			
			CoinFlipper.getEcomony().depositPlayer(Bukkit.getOfflinePlayer(winner), winMoney);
			
			//Sound
			try{
				p1.playSound(p1.getLocation(), Sound.valueOf(ConfigVar.SOUND_WINNER_CHOSEN.getString().toUpperCase()), 1F, 1F);
			}catch(Exception e){}
			try{
				p2.playSound(p2.getLocation(), Sound.valueOf(ConfigVar.SOUND_WINNER_CHOSEN.getString().toUpperCase()) , 1F, 1F);				}catch(Exception e){}
			
			String loser = s1;
			if (s1.equals(winner)){
				loser = s2;				
			}
			//Message winner/loser
			Player win = Bukkit.getPlayer(winner);
			if (win != null){
				
				String winMsg = Message.BET_WON.getMessage().replace("%MONEY%", winMoneyFormatted+"").replace("%WINNER%",
						winner).replace("%LOSER%", loser);
				win.sendMessage(winMsg);
				
				PacketUtils.sendTitle(win, Message.BET_TITLE_VICTORY.getMessage(), TitleType.TITLE, 20, 60, 20);
				PacketUtils.sendTitle(win, winMsg, TitleType.SUBTITLE, 20, 60, 20);
			}
			Player los = Bukkit.getPlayer(loser);
			if (los != null){
				String losMsg = Message.BET_LOST.getMessage().replace("%MONEY%", winMoneyFormatted+"").replace("%WINNER%",
						winner).replace("%LOSER%", loser);
				los.sendMessage(losMsg);
					
				PacketUtils.sendTitle(los, Message.BET_TITLE_LOSS.getMessage(), TitleType.TITLE, 20, 60, 20);
				PacketUtils.sendTitle(los, losMsg, TitleType.SUBTITLE, 20, 60, 20);
			}
			
			
			//Possibly broadcast win
			if ( !(ConfigVar.VALUE_NEEDED_TO_BROADCAST.getValue() == null) && 
					winMoney >= ConfigVar.VALUE_NEEDED_TO_BROADCAST.getDouble() && 
							ConfigVar.VALUE_NEEDED_TO_BROADCAST.getDouble() != -1){
					
				if (HookManager.getManager().isHooked(HookType.ChatPerWorld)){
					if (Bukkit.getPlayer(winner) == null){
						return;
					}
					Debug.print("ChatPerWorld broadcast.");
					for (Player oPl : Bukkit.getOnlinePlayers()){
						Debug.print("Testing player " + oPl.getName());
						if (HookChatPerWorld.getHook().getReceivers(Bukkit.getPlayer(winner)).contains(oPl)){
							Debug.print(oPl.getName() + " approved.");
							oPl.sendMessage(Message.HIGH_GAME_BROADCAST.getMessage()
									.replace("%MONEY%", winMoneyFormatted+"").replace("%WINNER%",
								winner).replace("%LOSER%", loser));
						}
					}
				}else{
					Debug.print("Normal broadcast.");
					Bukkit.broadcastMessage(Message.HIGH_GAME_BROADCAST.getMessage()
						.replace("%MONEY%", winMoneyFormatted+"").replace("%WINNER%",
								winner).replace("%LOSER%", loser));
				}
					
					
			}
		}
		
		
		//Sound click
		if (phase < winFrame && ConfigVar.ANIMATIONS_ENABLED.getBoolean()){
			try{
				p1.playSound(p1.getLocation(), Sound.valueOf(ConfigVar.SOUND_WHILE_CHOOSING.getString().toUpperCase()) , 1F, 1F);
			}catch(Exception e){}
			try{
				p2.playSound(p2.getLocation(), Sound.valueOf(ConfigVar.SOUND_WHILE_CHOOSING.getString().toUpperCase()) , 1F, 1F);
			}catch(Exception e){}
		}
		
		if (phase == (winFrame+20)){
			
			GamesManager.getManager().setSpinning(s1, false);
			GamesManager.getManager().setSpinning(s2, false);
			
			if (p1 != null && ConfigVar.GUI_AUTO_CLOSE.getBoolean()){
				p1.closeInventory();
			}
			if (p2 != null && ConfigVar.GUI_AUTO_CLOSE.getBoolean()){
				p2.closeInventory();
			}
		}
	}
}
