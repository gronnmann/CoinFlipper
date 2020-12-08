package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.command.CommandSender;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.command.CommandModule;
import io.github.gronnmann.coinflipper.customizable.Message;

public class CommandReload extends CommandModule{

	public CommandReload(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		System.out.println("[CoinFlipper] Attempting to reload CoinFlipper (requested by " + sender.getName() + ")");
		ConfigManager.getManager().reload();
		
		sender.sendMessage(Message.RELOAD_SUCCESS.getMessage());
	}
	
}
