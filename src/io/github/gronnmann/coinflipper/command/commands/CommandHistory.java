package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.command.CommandModule;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.history.HistoryManager;

public class CommandHistory extends CommandModule{

	public CommandHistory(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		
		Player p = null;
		if (args.length > 0) {
			if (!sender.hasPermission("coinflipper.history.other")) {
				sender.sendMessage(Message.NO_PERMISSION.getMessage());
				return;
			}
			
			if (targetCheck(sender, args[0])) {
				p = Bukkit.getPlayer(args[0]);
			}else return;
		}else {
			p = (Player)sender;
		}
		
		
		((Player)sender).openInventory(HistoryManager.getLogger().getHistory(p));
	}

}
