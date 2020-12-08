package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.command.CommandModule;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.stats.Stats;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;

public class CommandStats extends CommandModule{

	public CommandStats(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		
		Player p = null;
		
		
		//get right target
		if (args.length > 0) {
			if (!sender.hasPermission("coinflipper.stats.other")) {
				sender.sendMessage(Message.NO_PERMISSION.getMessage());
				return;
			}
			
			if (targetCheck(sender, args[0])) {
				p = Bukkit.getPlayer(args[0]);
			}else return;
		}else {
			if (!(sender instanceof Player)) {
				sender.sendMessage(Message.CMD_PLAYER_ONLY.getMessage());
				return;
			}
			p = (Player)sender;
		}
		
		Stats pS = StatsManager.getManager().getStats(p);
		if (pS == null){
			sender.sendMessage(Message.STATS_NOSTATS.getMessage());
			return;
		}
		String statsMessage = Message.STATS_STATS.getMessage().replace("%PLAYER%", p.getName()) + "\n"+ 
				Message.STATS_GAMESWON.getMessage().replace("%AMOUNT%", pS.getGamesWon()+"") + "\n" +
				Message.STATS_GAMESLOST.getMessage().replace("%AMOUNT%", pS.getGamesLost()+"")+ "\n" +
				Message.STATS_WINPERCENTAGE.getMessage().replace("%AMOUNT%", pS.getWinPercentage()+"")+ "\n" +
				Message.STATS_MONEYWON.getMessage().replace("%AMOUNT%", GeneralUtils.getFormattedNumbers(pS.getMoneyWon()))+ "\n" +
				Message.STATS_MONEYSPENT.getMessage().replace("%AMOUNT%", GeneralUtils.getFormattedNumbers(pS.getMoneySpent()))+ "\n" +
				Message.STATS_MONEYEARNED.getMessage().replace("%AMOUNT%", GeneralUtils.getFormattedNumbers(pS.getMoneyEarned()));
		sender.sendMessage(statsMessage);
		
		
	}
	
}
