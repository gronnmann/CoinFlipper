package io.github.gronnmann.coinflipper;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.events.BetPlaceEvent;
import io.github.gronnmann.coinflipper.gui.SelectionScreen;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import net.milkbowl.vault.economy.EconomyResponse;

public class GamesManager {
	private GamesManager(){}
	private static GamesManager mng = new GamesManager();
	public static GamesManager getManager(){
		return mng;
	}
	
	private ArrayList<String> alreadySpinning = new ArrayList<String>();
	
	public void setSpinning(String player, boolean isSpinning){
		if (isSpinning){
			alreadySpinning.add(player);
		}else{
			alreadySpinning.remove(player);
		}
	}
	
	public boolean isSpinning(String player){
		return alreadySpinning.contains(player);
	}
	
	public boolean createGame(Player p, int side, double mon){
		
		if (BettingManager.getManager().isAlreadyThere(p)){
			p.sendMessage(MessagesManager.getMessage(Message.PLACE_FAILED_ALREADYGAME));
			return false;
		}
		
		if (mon < ConfigManager.getManager().getConfig().getDouble("min_amount")){
			p.sendMessage(MessagesManager.getMessage(Message.MIN_BET).replaceAll("%MIN_BET%", GeneralUtils.getFormattedNumbers(ConfigManager.getManager().getConfig().getDouble("min_amount"))));
			return true;
		}
		if (mon > ConfigManager.getManager().getConfig().getDouble("max_amount")){
			p.sendMessage(MessagesManager.getMessage(Message.MAX_BET).replaceAll("%MAX_BET%", GeneralUtils.getFormattedNumbers(ConfigManager.getManager().getConfig().getDouble("max_amount"))));
			return true;
		}
		
		
		EconomyResponse response = Main.getEcomony().withdrawPlayer(p.getName(), mon);
		if (!response.transactionSuccess()){
			p.sendMessage(MessagesManager.getMessage(Message.PLACE_FAILED_NOMONEY));
			return false;
		}
		
		
		BetPlaceEvent placeEvent = new BetPlaceEvent(p, mon, side);			
		Bukkit.getPluginManager().callEvent(placeEvent);
		
		if (placeEvent.isCancelled()){
			Main.getEcomony().depositPlayer(p.getName(), mon);
			return false;
		}
		
		p.sendMessage(MessagesManager.getMessage(Message.PLACE_SUCCESSFUL));
		BettingManager.getManager().addBet(p, side, mon);
		SelectionScreen.getInstance().refreshGameManager();
		return true;
	}
}
