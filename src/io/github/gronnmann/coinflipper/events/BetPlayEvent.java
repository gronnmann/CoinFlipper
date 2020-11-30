package io.github.gronnmann.coinflipper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.gronnmann.coinflipper.animations.Animation;
import io.github.gronnmann.coinflipper.bets.Bet;

public class BetPlayEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList(){
		return handlers;
	}
	public HandlerList getHandlers(){
		return handlers;
	}
	
	private Animation anim;
	private String player1, player2, winner;
	private double winAmount;
	private Bet bet;
	
	public BetPlayEvent(String player1, String player2, String winner, Animation anim, double winMoney, Bet bet){
		this.player1 = player1;
		this.player2 = player2;
		this.winner = winner;
		this.anim = anim;
		this.winAmount = winMoney;
		this.bet =  bet;
	}
	
	public Animation getAnimation(){
		return anim;
	}
	public String getPlayerOne(){
		return player1;
	}
	public String getPlayerTwo(){
		return player2;
	}
	public String getWinner(){
		return winner;
	}
	public double getWinMoney(){
		return winAmount;
	}
	public Bet getBet(){
		return bet;
	}
}
