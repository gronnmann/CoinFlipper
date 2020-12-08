package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.command.CommandModule;
import io.github.gronnmann.coinflipper.customizable.Message;

public class CommandCreateGame extends CommandModule{

	
	public CommandCreateGame(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		
		Player p = (Player)sender;

		double i = 0;
		try{
			i = Double.parseDouble(args[0]);
			
		}catch(Exception e){
			p.sendMessage(Message.WRONG_MONEY.getMessage().replace("%NUMBER%", args[0]));
			return;
		}
		
		
		int side = -1;
		if (args[1].equalsIgnoreCase("heads")||args[1].equalsIgnoreCase("h")||args[1].equalsIgnoreCase(Message.HEADS.getMessage())){
			side = 1;
		}else if (args[1].equalsIgnoreCase("tails")||args[1].equalsIgnoreCase("t")||args[1].equalsIgnoreCase(Message.TAILS.getMessage())){
			side = 0;
		}else{
			p.sendMessage(Message.PLACE_TRIAL_PICKSIDE.getMessage());
			return;
		}
		
		
		GamesManager.getManager().createGame(p, side, i);
	}

}
