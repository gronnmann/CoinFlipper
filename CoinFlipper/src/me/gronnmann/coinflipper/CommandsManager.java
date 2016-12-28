package me.gronnmann.coinflipper;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.EconomyResponse;


public class CommandsManager implements CommandExecutor{
	
	private String help = ChatColor.GOLD + "Syntax:\n"
			+ ChatColor.GREEN + "/coinflipper [amount] [heads/tails]" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Places a CoinFlipper game\n"
			+ ChatColor.GREEN + "/coinflipper gui" + ChatColor.DARK_GREEN + " - " + ChatColor.YELLOW + "Opens the GUI";
	public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage("This command is for players only.");
			return true;
		}
		
		
		
		
		Player p = (Player)sender;
		
		/*/cf place [amount] [head/tails]
		 * cf gui
		 * 
		 */
		
			
		if (args.length == 2){
			if (!p.hasPermission("coinflipper.create")){
				p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
				return true;
			}
			double i = 0;
			try{
				i = Double.parseDouble(args[0]);
				
			}catch(Exception e){
				p.sendMessage(ChatColor.RED + args[0] + " is not recognized as a number.");
				return true;
			}
			
			if (i < ConfigManager.getManager().getConfig().getInt("min_amount")){
				p.sendMessage(ChatColor.RED + "Please provide a bet over " + ChatColor.GREEN + "$" + ConfigManager.getManager().getConfig().getInt("min_amount"));
				return true;
			}
			if (i > ConfigManager.getManager().getConfig().getInt("max_amount")){
				p.sendMessage(ChatColor.RED + "Please provide a bet under " + ChatColor.GREEN + "$" + ConfigManager.getManager().getConfig().getInt("max_amount"));
				return true;
			}
			
			
			int side = -1;
			if (args[1].equalsIgnoreCase("heads")||args[1].equalsIgnoreCase("h")){
				side = 1;
			}else if (args[1].equalsIgnoreCase("tails")||args[1].equalsIgnoreCase("t")){
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
				p.sendMessage(ChatColor.RED + "You already have a CoinFlipper game in progress.");
				return true;
			}
			
			
			EconomyResponse response = Main.getEcomony().withdrawPlayer(p.getName(), i);
			if (!response.transactionSuccess()){
				p.sendMessage(ChatColor.RED + "You don't have the money to do this.");
				return true;
			}
			
			
			
			p.sendMessage(ChatColor.GREEN + "Successfully placed a CoinFlipper game.");
			BettingManager.getManager().createBet(p, side, i);
			GUI.getInstance().refreshGameManager();
			
			
			
		}else if (args.length == 1){
			if (args[0].equalsIgnoreCase("help")){
				if (!p.hasPermission("coinflipper.help")){
					p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
					return true;
				}
				p.sendMessage(help);
				return true;
			}else if (args[0].equalsIgnoreCase("gui")){
				if (!p.hasPermission("coinflipper.gui")){
					p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
					return true;
				}
				GUI.getInstance().openGameManager(p);
				return true;
			}else if (args[0].equalsIgnoreCase("clear")){
				if (!p.hasPermission("coinflipper.clear")){
					p.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
					return true;
				}
				if (BettingManager.getManager().getBets().isEmpty()){
					p.sendMessage(ChatColor.RED + "There are no bets to clear.");
					return true;
				}
				BettingManager.getManager().clearBets();
				p.sendMessage(ChatColor.GREEN + "Cleared all bets.");
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
