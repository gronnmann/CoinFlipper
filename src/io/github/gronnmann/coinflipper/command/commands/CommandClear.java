package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.command.CommandModule;
import io.github.gronnmann.coinflipper.customizable.Message;

public class CommandClear extends CommandModule{

	public CommandClear(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		
		if (BettingManager.getManager().getBets().isEmpty()){
			sender.sendMessage(Message.CLEAR_FAILED_NOBETS.getMessage());
			return;
		}
		BettingManager.getManager().clearBets();
		sender.sendMessage(Message.CLEAR_SUCCESSFUL.getMessage());
	}

}
