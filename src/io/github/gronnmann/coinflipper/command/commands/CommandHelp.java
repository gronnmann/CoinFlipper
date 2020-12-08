package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.command.CommandSender;

import io.github.gronnmann.coinflipper.command.CommandModule;

public class CommandHelp extends CommandModule{
	public CommandHelp(String label, String permission, int minArgs, int maxArgs, boolean playerOnly, String help) {
		super(label, permission, minArgs, maxArgs, playerOnly);
		this.help = help;
	}


	String help = "";
	

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		sender.sendMessage(help);
	}
	
	
}
