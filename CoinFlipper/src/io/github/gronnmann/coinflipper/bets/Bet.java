package io.github.gronnmann.coinflipper.bets;

import io.github.gronnmann.coinflipper.ConfigManager;

public class Bet {
	private String player;
	private int bet, id, minsRemaining, booster;
	private double amount;
	
	
	public Bet(String player, int bet, double amount, int id, int booster){
		this.player = player;
		this.bet = bet;
		this.amount = amount;
		this.id = id;
		this.minsRemaining = ConfigManager.getManager().getConfig().getInt("time_expire");
		this.booster = booster;
	}
	
	public String getPlayer(){
		return player;
	}
	public int getSide(){
		return bet;
	}
	public double getAmount(){
		return amount;
	}
	public int getID(){
		return id;
	}
	public int getBooster(){
		return booster;
	}	
	public int getTimeRemaining(){
		return minsRemaining;
	}
	public void setTimeRemaining(int time){
		this.minsRemaining = time;
	}
}

