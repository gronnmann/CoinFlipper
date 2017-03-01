package io.github.gronnmann.coinflipper.bets;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.GUI;
import io.github.gronnmann.coinflipper.Main;
import io.github.gronnmann.coinflipper.animations.Animation;
import io.github.gronnmann.coinflipper.animations.AnimationsManager;
import io.github.gronnmann.coinflipper.stats.StatsManager;

public class BettingManager {
	private BettingManager(){}
	private static BettingManager manager = new BettingManager();
	public static BettingManager getManager(){
		return manager;
	}
	
	private ArrayList<Bet> bets = new ArrayList<Bet>();
	
	
	public Bet createBet(Player p, int side, double amount){
		//Booster
		int booster = 0;
		for (int i = 0;i<=100;i++){
			if (p.hasPermission("coinflipper.boost."+i)){
				if (i > booster){
					booster=i;
				}
			}
		}
		
		Animation animation = AnimationsManager.getManager().getAnimationToUse(p);
		
		//Rest
		Bet b = new Bet(p.getName(), side, amount, this.getNextAvaibleID(), booster, animation);
		bets.add(b);
		return b;
	}
	
	
	public int getNextAvaibleID(){
		if (bets.isEmpty()){
			return 1;
		}
		
		int greatestID = 1;
		
		for (Bet g : bets){
			if (g.getID() > greatestID){
				greatestID = g.getID();
			}
		}
		
		return greatestID+1;
			
	}
	
	public Bet getBet(int id){
		for (Bet g : bets){
			if (g.getID() == id){
				return g;
			}
		}
		
		return null;
	}
	
	
	public boolean betExists(int id){
		for (Bet g : bets){
			if (id == g.getID())return true;
		}
		return false;
	}
	
	public void removeBet(Bet g){
		bets.remove(g);
	}
	
	public void removeBet(int id){
		bets.remove(this.getBet(id));
	}
	
	public String challengeBet(Bet b, Player p){
		
		int[] chances = this.getChances(p, b);
		
		Random rn = new Random();
		int r = rn.nextInt(chances[0]+chances[1]);
		
		if (r <= chances[1]){
			StatsManager.getManager().getStats(p).addLose();
			StatsManager.getManager().getStats(Bukkit.getOfflinePlayer(b.getPlayer()).getUniqueId().toString()).addWin();
			return b.getPlayer();
		}else{
			StatsManager.getManager().getStats(p).addWin();
			StatsManager.getManager().getStats(Bukkit.getOfflinePlayer(b.getPlayer()).getUniqueId().toString()).addLose();
			return p.getName();
		}
	}
	
	public int[] getChances(Player p1, Bet b){
		int i1 = 50;
		int i2  = 50;
		
		int booster1 = 0, booster2 = 0;
		
		for (int i = 0; i<=100;i++){
			if (p1.hasPermission("coinflipper.boost."+i)){
				if (i > booster1){
					booster1 = i;
				}
			}
		}
		booster2 = b.getBooster();
		
		if (booster1 == 0 && booster2 != 0){
			i2 = booster2;
			i1 = 100-booster2;
		}
		else if (booster1 != 0 && booster2 == 0){
			i1 = booster1;
			i2 = 100-booster1;
		}
		else if (booster1 != 0 && booster2 != 0){
			i1 = booster1;
			i2 = booster2;
		}
		
		
		int[] returned = {i1, i2};
		return returned;
		
	}
	
	public ArrayList<Bet> getBets(){
		return bets;
	}
	
	public void clearBets(){
		for (Bet b : bets){
			Main.getEcomony().depositPlayer(b.getPlayer(), b.getAmount());
		}
		bets.clear();
		GUI.getInstance().refreshGameManager();
	}
	
}


