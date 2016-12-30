package me.gronnmann.coinflipper;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.gronnmann.coinflipper.MessagesManager.Message;
import me.gronnmann.coinflipper.bets.Bet;
import me.gronnmann.coinflipper.bets.BettingManager;
import me.gronnmann.coinflipper.stats.Stats;
import me.gronnmann.coinflipper.stats.StatsManager;
import net.milkbowl.vault.economy.EconomyResponse;


public class CommandsManager implements CommandExecutor{
	
	private String help = getMsg(Message.SYNTAX);
	
	private String getMsg(Message msg){
		return MessagesManager.getMessage(msg);
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage(getMsg(Message.CMD_PLAYER_ONLY));
			return true;
		}
		
		
		
		
		Player p = (Player)sender;
		
		/*/cf place [amount] [head/tails]
		 * cf gui
		 * 
		 */
		
			
		if (args.length == 2){
			if (!p.hasPermission("coinflipper.create")){
				p.sendMessage(getMsg(Message.NO_PERMISSION));
				return true;
			}
			double i = 0;
			try{
				i = Double.parseDouble(args[0]);
				
			}catch(Exception e){
				p.sendMessage(getMsg(Message.WRONG_MONEY).replaceAll("%NUMBER%", args[0]));
				return true;
			}
			
			if (i < ConfigManager.getManager().getConfig().getInt("min_amount")){
				p.sendMessage(getMsg(Message.MIN_BET).replaceAll("%MIN_BET%", ConfigManager.getManager().getConfig().getInt("min_amount")+""));
				return true;
			}
			if (i > ConfigManager.getManager().getConfig().getInt("max_amount")){
				p.sendMessage(getMsg(Message.MAX_BET).replaceAll("%MAX_BET%", ConfigManager.getManager().getConfig().getInt("max_amount")+""));
				return true;
			}
			
			
			int side = -1;
			if (args[1].equalsIgnoreCase("heads")||args[1].equalsIgnoreCase("h")||args[1].equalsIgnoreCase(getMsg(Message.HEADS))){
				side = 1;
			}else if (args[1].equalsIgnoreCase("tails")||args[1].equalsIgnoreCase("t")||args[1].equalsIgnoreCase(getMsg(Message.TAILS))){
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
				p.sendMessage(getMsg(Message.PLACE_FAILED_ALREADYGAME));
				return true;
			}
			
			
			EconomyResponse response = Main.getEcomony().withdrawPlayer(p.getName(), i);
			if (!response.transactionSuccess()){
				p.sendMessage(getMsg(Message.PLACE_FAILED_NOMONEY));
				return true;
			}
			
			
			
			p.sendMessage(getMsg(Message.PLACE_SUCCESSFUL));
			BettingManager.getManager().createBet(p, side, i);
			GUI.getInstance().refreshGameManager();
			
			
			
		}else if (args.length == 1){
			if (args[0].equalsIgnoreCase("help")){
				if (!p.hasPermission("coinflipper.help")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				p.sendMessage(help);
				return true;
			}else if (args[0].equalsIgnoreCase("gui")){
				if (!p.hasPermission("coinflipper.gui")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				GUI.getInstance().openGameManager(p);
				return true;
			}else if (args[0].equalsIgnoreCase("clear")){
				if (!p.hasPermission("coinflipper.clear")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				if (BettingManager.getManager().getBets().isEmpty()){
					p.sendMessage(getMsg(Message.CLEAR_FAILED_NOBETS));
					return true;
				}
				BettingManager.getManager().clearBets();
				p.sendMessage(getMsg(Message.CLEAR_SUCCESSFUL));
			}else if (args[0].equalsIgnoreCase("stats")){
				if (!p.hasPermission("coinflipper.stats")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				Stats pS = StatsManager.getManager().getStats(p);
				if (pS == null){
					p.sendMessage(getMsg(Message.STATS_NOSTATS));
					return true;
				}
				String statsMessage = getMsg(Message.STATS_STATS) + "\n"+ 
						getMsg(Message.STATS_GAMESWON).replaceAll("%AMOUNT%", pS.getGamesWon()+"") + "\n" +
						getMsg(Message.STATS_GAMESLOST).replaceAll("%AMOUNT%", pS.getGamesLost()+"")+ "\n" +
						getMsg(Message.STATS_WINPERCENTAGE).replaceAll("%AMOUNT%", pS.getWinPercentage()+"")+ "\n" +
						getMsg(Message.STATS_MONEYWON).replaceAll("%AMOUNT%", pS.getMoneyWon()+"")+ "\n" +
						getMsg(Message.STATS_MONEYSPENT).replaceAll("%AMOUNT%", pS.getMoneySpent()+"")+ "\n" +
						getMsg(Message.STATS_MONEYEARNED).replaceAll("%AMOUNT%", pS.getMoneyEarned()+"");
				p.sendMessage(statsMessage);
						
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
