package io.github.gronnmann.coinflipper;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class MessagesManager {
	public enum Message {NO_PERMISSION, CMD_PLAYER_ONLY, PLAYER_NOT_FOUND, HEADS, TAILS, WRONG_MONEY, MIN_BET, MAX_BET, 
		SYNTAX_L1, SYNTAX_L2, SYNTAX_L3, SYNTAX_L4,
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
		//File tempFolder = Files.createTempDir();
		File temp = File.createTempFile("tempMsg", ".yml");
		
		URL messagesOrginal = new URL("https://raw.githubusercontent.com/gronnmann/CoinFlipper/master/CoinFlipper/src/messages.yml");
		FileUtils.copyURLToFile(messagesOrginal, temp);
		
		FileConfiguration messagesOrg = YamlConfiguration.loadConfiguration(temp);
		
		return messagesOrg.getString(msg);
		
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("[CoinFlipper] Download of default message for " + msg + " failed. Please get orginal at https://www.spigotmc.org/resources/coinflipper.33916/");
			return "Message " + msg + " missing. Please fill it out or find orginal at https://www.spigotmc.org/resources/coinflipper.33916/";
		}
	}
}
