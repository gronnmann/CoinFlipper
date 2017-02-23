package io.github.gronnmann.coinflipper.bets;

import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.GUI;

public class BettingTimer extends BukkitRunnable{
	public void run(){
		if (BettingManager.getManager().getBets().isEmpty())return;
		for (Bet b : BettingManager.getManager().getBets()){
			b.setTimeRemaining(b.getTimeRemaining()-1);
			
			if (b.getTimeRemaining() == 0){
				BettingManager.getManager().removeBet(b);
			}
		}
		GUI.getInstance().refreshGameManager();
	}
}
