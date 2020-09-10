package io.github.gronnmann.coinflipper.bets;

import io.github.gronnmann.coinflipper.animations.Animation;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;

public class Bet {
	private String player;
	private int bet, id, minsRemaining, booster;
	private Animation animation;
	private double amount;
	
	
	public Bet(String player, int bet, double amount, int id, int booster, Animation animation){
		this.player = player;
		this.bet = bet;
		this.amount = amount;
		this.id = id;
		this.minsRemaining = ConfigVar.TIME_EXPIRE.getInt();
		this.booster = booster;
		this.animation = animation;
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
	public Animation getAnimation(){
		return animation;
	}
	public void setAnimation(Animation animation){
		this.animation = animation;
	}
}

