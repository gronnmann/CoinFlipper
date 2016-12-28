package me.gronnmann.coinflipper;

public class Bet {
	private String player;
	private int bet, id, minsRemaining;
	private double amount;
	
	
	public Bet(String player, int bet, double amount, int id){
		this.player = player;
		this.bet = bet;
		this.amount = amount;
		this.id = id;
		this.minsRemaining = ConfigManager.getManager().getConfig().getInt("time_expire");
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
	public int getTimeRemaining(){
		return minsRemaining;
	}
	public void setTimeRemaining(int time){
		this.minsRemaining = time;
	}
}

