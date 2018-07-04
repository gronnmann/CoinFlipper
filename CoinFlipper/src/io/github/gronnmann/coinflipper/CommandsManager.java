package io.github.gronnmann.coinflipper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.coinflipper.animations.AnimationFileManager;
import io.github.gronnmann.coinflipper.animations.AnimationGUI;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.gui.CreationGUI;
import io.github.gronnmann.coinflipper.gui.FileEditSelector;
import io.github.gronnmann.coinflipper.gui.SelectionScreen;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.mysql.SQLManager;
import io.github.gronnmann.coinflipper.stats.Stats;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;


public class CommandsManager implements CommandExecutor{
	
	private String help = getMsg(Message.SYNTAX_L1)+"\n"+getMsg(Message.SYNTAX_L2)+"\n"+
			getMsg(Message.SYNTAX_L3)+"\n"+getMsg(Message.SYNTAX_L4)+"\n" + getMsg(Message.SYNTAX_L5);
	
	private String getMsg(Message msg){
		return MessagesManager.getMessage(msg);
	}
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage(getMsg(Message.CMD_PLAYER_ONLY));
			return true;
		}
		
		Player p = (Player)sender;
		
		boolean enabledInWorld = true;
		for (String world : ConfigManager.getManager().getConfig().getString("disabled_worlds").split(",")){
			if (p.getWorld().getName().equals(world)){
				enabledInWorld = false;
			}
		}
		
		if (!enabledInWorld){
			p.sendMessage(MessagesManager.getMessage(Message.DISABLED_IN_WORLD));
			return true;
		}
		
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
			
			
			int side = -1;
			if (args[1].equalsIgnoreCase("heads")||args[1].equalsIgnoreCase("h")||args[1].equalsIgnoreCase(getMsg(Message.HEADS))){
				side = 1;
			}else if (args[1].equalsIgnoreCase("tails")||args[1].equalsIgnoreCase("t")||args[1].equalsIgnoreCase(getMsg(Message.TAILS))){
				side = 0;
			}else{
				p.sendMessage(getMsg(Message.PLACE_TRIAL_PICKSIDE));
				return true;
			}
			
			
			GamesManager.getManager().createGame(p, side, i);
			
			
			
		}else if (args.length == 1){
			if (args[0].equalsIgnoreCase(getMsg(Message.CMD_HELP)) || args[0].equalsIgnoreCase("?")){
				if (!p.hasPermission("coinflipper.help")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				p.sendMessage(help);
				
				return true;
			}else if (args[0].equalsIgnoreCase(getMsg(Message.CMD_GUI))){
				if (!p.hasPermission("coinflipper.gui")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				SelectionScreen.getInstance().openGameManager(p);
				return true;
			}else if (args[0].equalsIgnoreCase(getMsg(Message.CMD_CONFIG))){
				if (!p.hasPermission("coinflipper.config")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				FileEditSelector.getInstance().openConfigurator(p);
				return true;
			}
			else if (args[0].equalsIgnoreCase(getMsg(Message.CMD_CLEAR))){
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
			}else if (args[0].equalsIgnoreCase(getMsg(Message.CMD_STATS))){
				if (!p.hasPermission("coinflipper.stats")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				
				Stats pS = StatsManager.getManager().getStats(p);
				if (pS == null){
					p.sendMessage(getMsg(Message.STATS_NOSTATS));
					return true;
				}
				String statsMessage = getMsg(Message.STATS_STATS).replaceAll("%PLAYER%", p.getName()) + "\n"+ 
						getMsg(Message.STATS_GAMESWON).replaceAll("%AMOUNT%", pS.getGamesWon()+"") + "\n" +
						getMsg(Message.STATS_GAMESLOST).replaceAll("%AMOUNT%", pS.getGamesLost()+"")+ "\n" +
						getMsg(Message.STATS_WINPERCENTAGE).replaceAll("%AMOUNT%", pS.getWinPercentage()+"")+ "\n" +
						getMsg(Message.STATS_MONEYWON).replaceAll("%AMOUNT%", GeneralUtils.getFormattedNumbers(pS.getMoneyWon()))+ "\n" +
						getMsg(Message.STATS_MONEYSPENT).replaceAll("%AMOUNT%", GeneralUtils.getFormattedNumbers(pS.getMoneySpent()))+ "\n" +
						getMsg(Message.STATS_MONEYEARNED).replaceAll("%AMOUNT%", GeneralUtils.getFormattedNumbers(pS.getMoneyEarned()));
				p.sendMessage(statsMessage);
						
			}else if (args[0].equalsIgnoreCase("animation")||args[0].equalsIgnoreCase("anim")||args[0].equalsIgnoreCase(getMsg(Message.CMD_ANIMATION))){
				if (!p.hasPermission("coinflipper.animation")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				AnimationGUI.getManager().openGUI(p);
			}
			else if (args[0].equalsIgnoreCase(getMsg(Message.CMD_RELOAD))){
				if (!p.hasPermission("coinflipper.reload")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				System.out.println("[CoinFlipper] Attempting to reload CoinFlipper (requested by " + p.getName() + ")");
				ConfigManager.getManager().reload();
				MaterialsManager.setup(Main.getMain());
				SelectionScreen.getInstance().setup(Main.getMain());
				StatsManager.getManager().load();
				AnimationFileManager.getManager().setup(Main.getMain());
				AnimationGUI.getManager().setup();
				BettingManager.getManager().load();
				HookManager.getManager().registerHooks(Main.getMain());
				CreationGUI.getInstance().generatePreset();
				
				p.sendMessage(getMsg(Message.RELOAD_SUCCESS));
			}
			
			else if (args[0].equalsIgnoreCase(getMsg(Message.CMD_CONVERT))){
				if (!p.hasPermission("coinflipper.convert")){
					p.sendMessage(getMsg(Message.NO_PERMISSION));
					return true;
				}
				
				p.sendMessage(getMsg(Message.CONVERT_START));
				
				if (StatsManager.getManager().convertToSQLite()){
					p.sendMessage(getMsg(Message.CONVERT_SUCCESS));
				}else{
					p.sendMessage(getMsg(Message.CONVERT_FAIL));
				}
				
				
				return true;
			}
			
			else{
				p.sendMessage(help);
				return true;
			}
		}
		else{
			if (!p.hasPermission("coinflipper.gui")){
				p.sendMessage(getMsg(Message.NO_PERMISSION));
				return true;
			}
			SelectionScreen.getInstance().openGameManager(p);
			return true;
		}
		 
		 
		
		return true;
	}
}
