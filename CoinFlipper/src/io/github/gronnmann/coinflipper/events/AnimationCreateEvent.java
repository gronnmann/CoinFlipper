package io.github.gronnmann.coinflipper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AnimationCreateEvent extends Event{
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
	
	private String name; 
	
	public AnimationCreateEvent(String anim){
		this.name = anim;
	}
	public String getName(){
		return name;
	}
}
