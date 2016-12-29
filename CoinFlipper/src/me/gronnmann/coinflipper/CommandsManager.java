package me.gronnmann.coinflipper;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gronnmann.coinflipper.MessagesManager.Message;
import net.milkbowl.vault.economy.EconomyResponse;


public class CommandsManager implements CommandExecutor{
	
	private String help = MessagesManager.getMessage(Message.SYNTAX);
	
	public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage(MessagesManager.getMessage(Message.CMD_PLAYER_ONLY));
			return true;
		}
		
		
		
		
		Player p = (Player)sender;
		
		/*/cf place [amount] [head/tails]
		 * cf gui
		 * 
		 */
		
			
		if (args.length == 2){
			if (!p.hasPermission("coinflipper.create")){
				p.sendMessage(MessagesManager.getMessage(Message.NO_PERMISSION));
				return true;
			}
			double i = 0;
			try{
				i = Double.parseDouble(args[0]);
				
			}catch(Exception e){
				p.sendMessage(MessagesManager.getMessage(Message.WRONG_MONEY).replaceAll("%NUMBER%", args[0]));
				return true;
			}
			
			if (i < ConfigManager.getManager().getConfig().getInt("min_amount")){
				p.sendMessage(MessagesManager.getMessage(Message.MIN_BET).replaceAll("%MIN_BET%", ConfigManager.getManager().getConfig().getInt("min_amount")+""));
				return true;
			}
			if (i > ConfigManager.getManager().getConfig().getInt("max_amount")){
				p.sendMessage(MessagesManager.getMessage(Message.MAX_BET).replaceAll("%MAX_BET%", ConfigManager.getManager().getConfig().getInt("max_amount")+""));
				return true;
			}
			
			
			int side = -1;
			if (args[1].equalsIgnoreCase("heads")||args[1].equalsIgnoreCase("h")||args[1].equalsIgnoreCase(MessagesManager.getMessage(Message.HEADS))){
				side = 1;
			}else if (args[1].equalsIgnoreCase("tails")||args[1].equalsIgnoreCase("t")||args[1].equalsIgnoreCase(MessagesManager.getMessage(Message.TAILS))){
				side = 0;
			}else{
				p.sendMessage(ChatColor.RED + "Please choose a site. (heads/tails)");
				return true;
			}
			
			
			boolean isAlreadyThere = false;
			
			for (Bet b : BettingManager.getManager().getBets()){
				if (b.getPlayer().equals(p.getName())){
					isAlreadyThere = true;
				}
			}
			
			if (isAlreadyThere){
				p.sendMessage(MessagesManager.getMessage(Message.PLACE_FAILED_ALREADYGAME));
				return true;
			}
			
			
			EconomyResponse response = Main.getEcomony().withdrawPlayer(p.getName(), i);
			if (!response.transactionSuccess()){
				p.sendMessage(MessagesManager.getMessage(Message.PLACE_FAILED_NOMONEY));
				return true;
			}
			
			
			
			p.sendMessage(MessagesManager.getMessage(Message.PLACE_SUCCESSFUL));
			BettingManager.getManager().createBet(p, side, i);
			GUI.getInstance().refreshGameManager();
			
			
			
		}else if (args.length == 1){
			if (args[0].equalsIgnoreCase("help")){
				if (!p.hasPermission("coinflipper.help")){
					p.sendMessage(MessagesManager.getMessage(Message.NO_PERMISSION));
					return true;
				}
				p.sendMessage(help);
				return true;
			}else if (args[0].equalsIgnoreCase("gui")){
				if (!p.hasPermission("coinflipper.gui")){
					p.sendMessage(MessagesManager.getMessage(Message.NO_PERMISSION));
					return true;
				}
				GUI.getInstance().openGameManager(p);
				return true;
			}else if (args[0].equalsIgnoreCase("clear")){
				if (!p.hasPermission("coinflipper.clear")){
					p.sendMessage(MessagesManager.getMessage(Message.NO_PERMISSION));
					return true;
				}
				if (BettingManager.getManager().getBets().isEmpty()){
					p.sendMessage(MessagesManager.getMessage(Message.CLEAR_FAILED_NOBETS));
					return true;
				}
				BettingManager.getManager().clearBets();
				p.sendMessage(MessagesManager.getMessage(Message.CLEAR_SUCCESSFUL));
			}else{
				p.sendMessage(help);
				return true;
			}
		}
		else{
			p.sendMessage(help);
			return true;
		}
		 
		 
		
		return true;
	}
}
