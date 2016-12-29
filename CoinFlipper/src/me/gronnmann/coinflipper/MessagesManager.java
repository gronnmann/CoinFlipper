package me.gronnmann.coinflipper;

import org.bukkit.ChatColor;

public class MessagesManager {
	public enum Message {NO_PERMISSION, CMD_PLAYER_ONLY, HEADS, TAILS, WRONG_MONEY, SYNTAX, MIN_BET, MAX_BET, 
		PLACE_SUCCESSFUL, PLACE_FAILED_ALREADYGAME,PLACE_FAILED_NOMONEY,
		CLEAR_SUCCESSFUL, CLEAR_FAILED_NOBETS,
		HELP_ITEM_L1,HELP_ITEM_L2,HELP_ITEM_L3, HELP_ITEM_L4,
		BET_REMOVE_SELF_CONFIRM, BET_REMOVE_SELF_SUCCESSFUL, BET_REMOVE_OTHER_CONFIRM, BET_REMOVE_OTHER_SUCCESSFUL, BET_REMOVE_OTHER_NOTIFICATION,
		BET_CHALLENGE_CANTSELF, BET_CHALLENGE_NOMONEY, BET_WON,
		MENU_HEAD_PLAYER, MENU_HEAD_MONEY, MENU_HEAD_TIMEREMAINING, MENU_HEAD_GAME, MENU_HEAD_SIDE}
	private MessagesManager(){}
	
	public static String getMessage(Message msg){
		if (msg.equals(Message.SYNTAX)){
			String syntax = ConfigManager.getManager().getMessages().getString("SYNTAX_L1") +"\n"+ 
					ConfigManager.getManager().getMessages().getString("SYNTAX_L2") +"\n"+
					ConfigManager.getManager().getMessages().getString("SYNTAX_L3");
			return ChatColor.translateAlternateColorCodes('&', syntax);
		}
		return ChatColor.translateAlternateColorCodes('&', ConfigManager.getManager().getMessages().getString(msg.toString()));
	}
}
