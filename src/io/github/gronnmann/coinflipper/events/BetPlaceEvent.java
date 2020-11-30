package io.github.gronnmann.coinflipper.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BetPlaceEvent extends Event implements Cancellable{
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
	private double money;
	private int side;
	
	public BetPlaceEvent(Player player, double amount, int side){
		this.cancelled = false;
		this.player = player;
		this.money = amount;
		this.side = side;
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public double getMoney(){
		return money;
	}
	public int getSide(){
		return side;
	}
}
