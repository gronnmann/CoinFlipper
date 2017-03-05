package io.github.gronnmann.coinflipper.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.gronnmann.coinflipper.animations.Animation;

public class AnimationFrameChangeEvent extends Event{
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
	private int current, next;
	
	public AnimationFrameChangeEvent(Animation animation, int currentFrame, int nextFrame){
		this.animation = animation;
		this.current = currentFrame;
		this.next = nextFrame;
	}
	
	public Animation getAnimation(){
		return animation;
	}
	
	public int getCurrentFrame(){
		return current;
	}
	public int getNextFrame(){
		return next;
	}
}
