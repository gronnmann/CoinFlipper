package io.github.gronnmann.coinflipper.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.gronnmann.coinflipper.bets.Bet;

public class BetChallengeEvent extends Event implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList(){
		return handlers;
	}
	public HandlerList getHandlers(){
		return handlers;
	}
	
	private boolean cancelled;
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}
	public boolean isCancelled(){
		return cancelled;
	}
	
	private Player player;
	private Bet bet;
	
	public BetChallengeEvent(Player player, Bet bet){
		this.cancelled = false;
		this.player = player;
		this.bet = bet;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public Bet getBet(){
		return bet;
	}
}
