package io.github.gronnmann.coinflipper.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import io.github.gronnmann.coinflipper.customizable.Message;

public abstract class CommandModule {
	
	private List<String> labels;


	private String permission;
	private int minArgs;
	private int maxArgs;
	
	private boolean playerOnly;
	
	
	
	public CommandModule(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		this(new String[] {label}, permission, minArgs, maxArgs, playerOnly);
	}
	public CommandModule(String[] label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		this.labels = Arrays.asList(label);
		this.permission = permission;
		this.minArgs = minArgs;
		this.maxArgs = (maxArgs == -1) ? Integer.MAX_VALUE : maxArgs;
		this.playerOnly = playerOnly;
	}
	
	
	
	public List<String> getLabels() {
		return labels;
	}


	public String getPermission() {
		return permission;
	}


	public int getMinArgs() {
		return minArgs;
	}



	public int getMaxArgs() {
		return maxArgs;
	}
	
	public boolean isPlayerOnly() {
		return playerOnly;
	}
	
	
	public abstract void runCommand(CommandSender sender, String[] args);
	
	
	protected boolean targetCheck(CommandSender sender, String target) {
		if (Bukkit.getPlayer(target) == null) {
			sender.sendMessage(Message.PLAYER_NOT_FOUND.getMessage());
			return false;
		}
		return true;
	}
	
}
