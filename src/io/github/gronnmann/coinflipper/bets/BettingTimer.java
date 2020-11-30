package io.github.gronnmann.coinflipper.bets;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.gui.SelectionScreen;

public class BettingTimer extends BukkitRunnable{
	public void run(){
		if (BettingManager.getManager().getBets().isEmpty())return;
		
		ArrayList<Bet> toRemove = new ArrayList<Bet>();
		
		
		for (Bet b : BettingManager.getManager().getBets()){
			b.setTimeRemaining(b.getTimeRemaining()-1);
			
			if (b.getTimeRemaining() == 0){
				toRemove.add(b);
			}
		}
		
		
		
		
		for (Bet b : toRemove){
			
			CoinFlipper.getEcomony().depositPlayer(b.getPlayer(), b.getAmount());
			
			Player pB = Bukkit.getPlayer(b.getPlayer());
			if (pB != null) {
				pB.sendMessage(Message.BET_EXPIRE_REFUND.getMessage().replace("%MONEY%", b.getAmount()+""));
			}
			
			BettingManager.getManager().removeBet(b);
		}
		
		
		
		
		
		SelectionScreen.getInstance().refreshGameManager();
	}
}
