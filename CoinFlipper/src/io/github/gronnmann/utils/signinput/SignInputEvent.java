package io.github.gronnmann.utils.signinput;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SignInputEvent extends Event{
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList(){
		return handlers;
	}
	public HandlerList getHandlers(){
		return handlers;
	}
	
	private String[] input;
	private Player player;
	
	public SignInputEvent(String[] lines, Player player){
		this.input = lines;
		this.player = player;
	}
	
	public Player getPlayer(){
		return player;
	}
	public String getLine(int num){
		return input[num];
	}
	public String[] getLines(){
		return input;
	}
}
