package io.github.gronnmann.coinflipper;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.Message;
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
			p.sendMessage(Message.PLACE_FAILED_ALREADYGAME.getMessage());
			return false;
		}
		
		int maxBets = ConfigVar.MAX_BETS.getInt();
		if (BettingManager.getManager().getBets().size() >= maxBets && maxBets != -1) {
			p.sendMessage(Message.PLACE_FAILED_MAXBETS.getMessage().replace("%NUM%", maxBets+""));
			return false;
		}
		
		if (mon < ConfigVar.MIN_AMOUNT.getDouble()){
			p.sendMessage(Message.MIN_BET.getMessage().replace("%MIN_BET%", GeneralUtils.getFormattedNumbers(ConfigVar.MIN_AMOUNT.getDouble())));
			return false;
		}
		if (mon > ConfigVar.MAX_AMOUNT.getDouble()){
			p.sendMessage(Message.MAX_BET.getMessage().replace("%MAX_BET%", GeneralUtils.getFormattedNumbers(ConfigVar.MAX_AMOUNT.getDouble())));
			return false;
		}
		
		
		EconomyResponse response = CoinFlipper.getEcomony().withdrawPlayer(p.getName(), mon);
		if (!response.transactionSuccess()){
			p.sendMessage(Message.PLACE_FAILED_NOMONEY.getMessage());
			return false;
		}
		
		
		BetPlaceEvent placeEvent = new BetPlaceEvent(p, mon, side);			
		Bukkit.getPluginManager().callEvent(placeEvent);
		
		if (placeEvent.isCancelled()){
			CoinFlipper.getEcomony().depositPlayer(p.getName(), mon);
			return false;
		}
		
		p.sendMessage(Message.PLACE_SUCCESSFUL.getMessage());
		BettingManager.getManager().addBet(p, side, mon);
		SelectionScreen.getInstance().refreshGameManager();
		return true;
	}
}
