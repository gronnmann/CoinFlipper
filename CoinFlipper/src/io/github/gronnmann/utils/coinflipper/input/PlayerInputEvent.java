package io.github.gronnmann.utils.coinflipper.input;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInputEvent extends Event implements Cancellable{
	
	private boolean cancelled = false;
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	private static HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList(){
		return handlers;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
		
	}
	
	
	private Player player;
	private InputData params;
	private Object data;
	
	private boolean exiting = false;
	
	public PlayerInputEvent(Player player, InputData params, Object data) {
		this.player = player;
		this.params = params;
		this.data = data;
	}
	
	public PlayerInputEvent(Player player, InputData params, Object data, boolean exiting) {
		this(player, params, data);
		this.exiting = exiting;
	}
	
	
	public Player getPlayer() {
		return player;
	}
	public InputData getParams() {
		return params;
	}
	public Object getData() {
		return data;
	}
	
	public boolean isExiting() {
		return exiting;
	}

}
