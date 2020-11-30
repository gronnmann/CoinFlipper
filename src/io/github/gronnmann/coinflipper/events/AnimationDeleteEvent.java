package io.github.gronnmann.coinflipper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.gronnmann.coinflipper.animations.Animation;

public class AnimationDeleteEvent extends Event{
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
	
	private Animation animation;
	
	public AnimationDeleteEvent(Animation animation){
		this.animation = animation;
	}
	
	public Animation getAnimation(){
		return animation;
	}
}
