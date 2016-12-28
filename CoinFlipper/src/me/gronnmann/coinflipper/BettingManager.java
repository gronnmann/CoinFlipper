package me.gronnmann.coinflipper;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.entity.Player;

public class BettingManager {
	private BettingManager(){}
	private static BettingManager manager = new BettingManager();
	public static BettingManager getManager(){
		return manager;
	}
	
	private ArrayList<Bet> bets = new ArrayList<Bet>();
	
	
	public Bet createBet(Player p, int side, double amount){
		Bet b = new Bet(p.getName(), side, amount, this.getNextAvaibleID());
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
		Random rn = new Random();
		int r = rn.nextInt(2);
		if (r == 0){
			return b.getPlayer();
		}else{
			return p.getName();
		}
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


