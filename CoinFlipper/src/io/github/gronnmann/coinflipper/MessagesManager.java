package io.github.gronnmann.coinflipper;

import org.bukkit.ChatColor;

public class MessagesManager {
	public enum Message {NO_PERMISSION, CMD_PLAYER_ONLY, HEADS, TAILS, WRONG_MONEY, SYNTAX, MIN_BET, MAX_BET, 
		PLACE_SUCCESSFUL, PLACE_FAILED_ALREADYGAME,PLACE_FAILED_NOMONEY,
		CLEAR_SUCCESSFUL, CLEAR_FAILED_NOBETS,
		HELP_ITEM_L1,HELP_ITEM_L2,HELP_ITEM_L3, HELP_ITEM_L4,
		BET_REMOVE_SELF_CONFIRM, BET_REMOVE_SELF_SUCCESSFUL, BET_REMOVE_OTHER_CONFIRM, BET_REMOVE_OTHER_SUCCESSFUL, BET_REMOVE_OTHER_NOTIFICATION,
		BET_CHALLENGE_CANTSELF, BET_CHALLENGE_NOMONEY, BET_WON,
		MENU_HEAD_PLAYER, MENU_HEAD_MONEY, MENU_HEAD_TIMEREMAINING, MENU_HEAD_GAME, MENU_HEAD_SIDE,
		STATS_NOSTATS, STATS_STATS, STATS_GAMESWON, STATS_GAMESLOST, STATS_WINPERCENTAGE, STATS_MONEYWON, STATS_MONEYSPENT, STATS_MONEYEARNED}
	private MessagesManager(){}
	
	public static String getMessage(Message msg){
		
		try{
			if (msg.equals(Message.SYNTAX)){
				String syntax = ConfigManager.getManager().getMessages().getString("SYNTAX_L1") +"\n"+ 
						ConfigManager.getManager().getMessages().getString("SYNTAX_L2") +"\n"+
						ConfigManager.getManager().getMessages().getString("SYNTAX_L3") + "\n"+
						ConfigManager.getManager().getMessages().getString("SYNTAX_L4");
				return ChatColor.translateAlternateColorCodes('&', syntax);
			}
			return ChatColor.translateAlternateColorCodes('&', ConfigManager.getManager().getMessages().getString(msg.toString()));
		}catch(Exception e){
			if (msg.equals(Message.SYNTAX)){
				if (ConfigManager.getManager().getMessages().getString(msg.toString()+"_L1") == null){
					ConfigManager.getManager().getMessages().set(msg.toString()+"_L1", 
							"Default message avaible at: https://www.spigotmc.org/resources/coinflipper.33916/");
				}
				if (ConfigManager.getManager().getMessages().getString(msg.toString()+"_L2") == null){
					ConfigManager.getManager().getMessages().set(msg.toString()+"_L2", 
							"Default message avaible at: https://www.spigotmc.org/resources/coinflipper.33916/");
				}
				if (ConfigManager.getManager().getMessages().getString(msg.toString()+"_L3") == null){
					ConfigManager.getManager().getMessages().set(msg.toString()+"_L3", 
							"Default message avaible at: https://www.spigotmc.org/resources/coinflipper.33916/");
				}
				if (ConfigManager.getManager().getMessages().getString(msg.toString()+"_L4") == null){
					ConfigManager.getManager().getMessages().set(msg.toString()+"_L4", 
							"Default message avaible at: https://www.spigotmc.org/resources/coinflipper.33916/");
				}
			}else{
				ConfigManager.getManager().getMessages().set(msg.toString(), 
						"Default message avaible at: https://www.spigotmc.org/resources/coinflipper.33916/");
			}
			
			ConfigManager.getManager().saveMessages();
			System.out.println("[CoinFlipper] Message " + msg.toString() + " not found. Creating blank space for new.");
			return getMessage(msg);
		}
		
		
	}
}
