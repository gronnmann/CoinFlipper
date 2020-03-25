package io.github.gronnmann.coinflipper;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesManager {
	public enum Message {NO_PERMISSION, CMD_PLAYER_ONLY, PLAYER_NOT_FOUND, DISABLED_IN_WORLD, HEADS, TAILS, WRONG_MONEY, MIN_BET, MAX_BET, 
		CMD_GUI, CMD_STATS, CMD_ANIMATION, CMD_CLEAR, CMD_HELP, CMD_RELOAD, CMD_CONFIG, CMD_CONVERT,
		SYNTAX_L1, SYNTAX_L2, SYNTAX_L3, SYNTAX_L4, SYNTAX_L5,
		CONVERT_START, CONVERT_FAIL, CONVERT_SUCCESS,
		PLACE_TRIAL_PICKSIDE,PLACE_SUCCESSFUL, PLACE_FAILED_ALREADYGAME,PLACE_FAILED_NOMONEY,PLACE_NOT_POSSIBLE_NOMONEY,
		CLEAR_SUCCESSFUL, CLEAR_FAILED_NOBETS,
		HELP_ITEM_L1,HELP_ITEM_L2,HELP_ITEM_L3, HELP_ITEM_L4,
		CREATE,
		BET_REMOVE_SELF_CONFIRM, BET_REMOVE_SELF_SUCCESSFUL, BET_REMOVE_OTHER_CONFIRM, BET_REMOVE_OTHER_SUCCESSFUL, BET_REMOVE_OTHER_NOTIFICATION,
		BET_CHALLENGE_CANTSELF, BET_CHALLENGE_ALREADYSPINNING, BET_CHALLENGE_NOMONEY,
		BET_WON, BET_LOST, BET_TITLE_VICTORY, BET_TITLE_LOSS, HIGH_GAME_BROADCAST,
		BET_START_COMBAT, BET_TITLE_COMBAT,
		MENU_HEAD_PLAYER, MENU_HEAD_MONEY, MENU_HEAD_TIMEREMAINING, MENU_HEAD_GAME, MENU_HEAD_SIDE,
		STATS_NOSTATS, STATS_STATS, STATS_GAMESWON, STATS_GAMESLOST, STATS_WINPERCENTAGE, STATS_MONEYWON, STATS_MONEYSPENT, STATS_MONEYEARNED,
		RELOAD_SUCCESS,
		GUI_SELECTION, GUI_CONFIGURATOR, GUI_GAME, GUI_GAME_18,
		ANIMATION_CREATE_GIVENAME, ANIMATION_CREATE_ALREADYEXISTS, ANIMATION_CREATE_SUCCESS,ANIMATION_CLONE_SUCCESS , ANIMATION_CLONE_GIVENAME, 
		ANIMATION_REMOVE_SUCCESS, ANIMATION_REMOVE_CANT_REMOVE_DEFAULT, ANIMATION_REMOVE_CANT_REMOVE_ALL,
		ANIMATION_GUI_CREATE, ANIMATION_GUI_DELETE, ANIMATION_GUI_CLONE, ANIMATION_GUI_DEFANIM, 
		ANIMATION_FRAMEEDITOR_BACK, ANIMATION_FRAMEEDITOR_COPYLAST, ANIMATION_FRAMEEDITOR_P1HEAD, ANIMATION_FRAMEEDITOR_P2HEAD, ANIMATION_FRAMEEDITOR_WINNERHEAD,
		ANIMATION_FRAMEEDITOR_NEXT, ANIMATION_FRAMEEDITOR_CURRENT, ANIMATION_FRAMEEDITOR_PREV,
		ANIMATION_ROLL_P1SKULL, ANIMATION_ROLL_P2SKULL, ANIMATION_ROLL_WINNERSKULL, ANIMATION_ROLL_ROLLING, ANIMATION_ROLL_WINNERCHOSEN,
		CREATION_NAME, CREATION_MONEY, CREATION_SIDE, CREATION_BET,
		CREATION_MONEY_COLOR, CREATION_MONEY_T1, CREATION_MONEY_T2, CREATION_MONEY_T3, CREATION_MONEY_T4, CREATION_MONEY_T5, CREATION_MONEY_MAX,
		CREATION_MONEY_LEFTTOADD, CREATION_MONEY_RIGHTTOREMOVE,
		CREATION_MONEY_CUSTOM, CREATION_MONEY_CUSTOM_DESC, CREATION_MONEY_CUSTOM_SPEC, CREATION_MONEY_CUSTOM_TOOMUCH, CREATION_MONEY_CUSTOM_TOOLITTLE, CREATION_MONEY_CUSTOM_NOMONEY, CREATION_MONEY_CUSTOM_SUCCESS,
		INPUT_NOTNUM,
		CONFIGURATOR_EDIT_SUCCESSFUL, CONFIGURATOR_SPEC,
		CONFIGURATOR_MESSAGE_PREVIEW, CONFIGURATOR_MESSAGE_CONFIRM, CONFIGURATOR_MESSAGE_CHANGE, CONFIGURATOR_MESSAGE_CANCEL,
		
	
	}
	private MessagesManager(){}
	
	public static String getMessage(Message msg){
		
		try{
			return ChatColor.translateAlternateColorCodes('&', ConfigManager.getManager().getMessages().getString(msg.toString()));
		}catch(Exception e){
			ConfigManager.getManager().getMessages().set(msg.toString(), 
						MessagesManager.getOrginalMessage(msg.toString()));
			ConfigManager.getManager().saveMessages();
			System.out.println("[CoinFlipper] Message " + msg.toString() + " not found. Creating blank space for new.");
			return getMessage(msg);
		}
		
		
	}
	
	public static String getOrginalMessage(String msg){
		try{

		
		InputStream defaultFile = CoinFlipper.getMain().getClass().getResourceAsStream("/messages.yml");
		
		if (defaultFile == null)return "Message " + msg + " missing. Please fill it out or find orginal at https://www.spigotmc.org/resources/coinflipper.33916/";

		InputStreamReader fileReader = new InputStreamReader(defaultFile);
				
		FileConfiguration messagesOrg = YamlConfiguration.loadConfiguration(fileReader);
		
		String msgS = messagesOrg.getString(msg);
		
		fileReader.close();
		defaultFile.close();
		
		if (msgS == null){
			return "Message " + msg + " missing. Please fill it out or find orginal at https://www.spigotmc.org/resources/coinflipper.33916/";
		}
		return msgS;
		
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("[CoinFlipper] Download of default message for " + msg + " failed. Please get orginal at https://www.spigotmc.org/resources/coinflipper.33916/");
			return "Message " + msg + " missing. Please fill it out or find orginal at https://www.spigotmc.org/resources/coinflipper.33916/";
		}
	}
}
