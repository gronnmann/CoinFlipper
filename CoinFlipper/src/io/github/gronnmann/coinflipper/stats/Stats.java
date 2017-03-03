package io.github.gronnmann.coinflipper.stats;

import java.text.DecimalFormat;

public class Stats {
	private int gamesWon, gamesLost;
	private double moneyUsed, moneyWon, moneyEarned;
	
	public Stats(int gamesWon, int gamesLost, double moneyUsed, double moneyWon){
		this.gamesWon = gamesWon;
		this.gamesLost = gamesLost;
		this.moneyUsed = moneyUsed;
		this.moneyWon = moneyWon;
	}
	
	public int getGamesWon(){
		return gamesWon;
	}
	public int getGamesLost(){
		return gamesLost;
	}
	public double getMoneySpent(){
		return moneyUsed;
	}
	public double getMoneyWon(){
		return moneyWon;
	}
	public double getMoneyEarned(){
		return moneyEarned;
	}
	public double getWinPercentage(){
		double total = gamesWon+gamesLost;
		
		if (total == 0){
			return 0;
		}
		
		double percentage1 = (gamesWon/total) * 100;
		DecimalFormat df = new DecimalFormat("##0.00");
		
		return Double.parseDouble(df.format(percentage1).replace(',', '.'));
	}
	
	
	public void addWin(){
		gamesWon++;
	}
	public void addLose(){
		gamesLost++;
	}
	public void addMoneySpent(double amount){
		moneyUsed = moneyUsed+amount;
		this.calculateEarned();
	}
	public void addMoneyWon(double amount){
		moneyWon = moneyWon + amount;
		this.calculateEarned();
	}
	
	private void calculateEarned(){
		moneyEarned = moneyWon-moneyUsed;
	}
	
}
