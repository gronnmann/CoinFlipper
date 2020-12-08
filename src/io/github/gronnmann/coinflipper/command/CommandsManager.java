package io.github.gronnmann.coinflipper.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.command.commands.CommandAnimation;
import io.github.gronnmann.coinflipper.command.commands.CommandClear;
import io.github.gronnmann.coinflipper.command.commands.CommandConfigEditor;
import io.github.gronnmann.coinflipper.command.commands.CommandCreateGame;
import io.github.gronnmann.coinflipper.command.commands.CommandGUI;
import io.github.gronnmann.coinflipper.command.commands.CommandHelp;
import io.github.gronnmann.coinflipper.command.commands.CommandHistory;
import io.github.gronnmann.coinflipper.command.commands.CommandReload;
import io.github.gronnmann.coinflipper.command.commands.CommandStats;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.utils.coinflipper.Debug;

public class CommandsManager implements CommandExecutor{
	
	private ArrayList<CommandModule> modules = new ArrayList<CommandModule>();
	
	private String help;
	
	private CommandModule creationModule, guiCmd;
	
	//setup commands
	public CommandsManager() {
		help = Message.SYNTAX_L1.getMessage()+"\n"+Message.SYNTAX_L2.getMessage()+"\n"+
				Message.SYNTAX_L3.getMessage()+"\n"+Message.SYNTAX_L4.getMessage()+"\n" + Message.SYNTAX_L5.getMessage();
		
		
		//add all commands
		
		modules.add(new CommandHelp("help", "help", 0, -1, false, help));
		guiCmd = new CommandGUI(new String[] {Message.CMD_GUI.getMessage()}, "gui", 0, -1, true);
		modules.add(guiCmd);
		modules.add(new CommandClear(Message.CMD_CLEAR.getMessage(), "clear", 0, -1, false));
		modules.add(new CommandStats(Message.CMD_STATS.getMessage(), "stats", 0, -1, false));
		modules.add(new CommandHistory(Message.CMD_HISTORY.getMessage(), "history", 0, -1, true));
		modules.add(new CommandAnimation(new String[] {Message.CMD_ANIMATION.getMessage(), "animation", "anim"}, "animation", 0, -1, true));
		modules.add(new CommandReload(Message.CMD_RELOAD.getMessage(), "reload", 0, -1, false));
		modules.add(new CommandConfigEditor(Message.CMD_CONFIG.getMessage(), "config", 0, -1, true));
		
		
		creationModule = new CommandCreateGame("create", "create", 2, 2, true);
	}
	
	
	
	
	//send command to right command-module
	public boolean onCommand(CommandSender sender, Command cmd, String cfL, String[] args) {
		
		int argsNum = args.length;
		boolean senderPlayer = (sender instanceof Player);
		
		if (argsNum == 0) {
			if (senderPlayer) {
				if (manageCommand(guiCmd, sender, argsNum, senderPlayer)) {
					guiCmd.runCommand(sender, args);
				};
				return true;
			}else {
				sender.sendMessage(help);
				return true;
			}
		}
		
		if (argsNum > 0) {
			for (CommandModule m : modules) {
				
				//Debug.print(m.getLabels().toString() + ":" + m.getMinArgs() + ":" + m.getMaxArgs() + ":" + args[0].toLowerCase());
				
				
				boolean match = false;
				String cmdL = args[0];
				for (String l : m.getLabels()) {
					if (l.equalsIgnoreCase(cmdL)) {
						match = true;
						break;
					}
				}
				if (!match)continue;
				
				if (!manageCommand(m, sender, argsNum-1, senderPlayer))return true;
				
				
				
				List<String> argsAsList = new LinkedList<>(Arrays.asList(args));
				argsAsList.remove(0);
				m.runCommand(sender, argsAsList.toArray(new String[argsNum-1]));
				return true;
				
			}
			
			try {
				Double checkIfGameCreation = Double.parseDouble(args[0]);
				
				if (manageCommand(creationModule, sender, argsNum, senderPlayer)) {
					creationModule.runCommand(sender, args);
				}
				return true;
			}catch(Exception e) {
				sender.sendMessage(help);
			}
		}
		
		return true;
	}
	
	private boolean manageCommand(CommandModule m, CommandSender sender, int argsNum, boolean senderPlayer) {
		Debug.print(argsNum + ":" + m.getMaxArgs() + ":" + m.getMinArgs());
		if (argsNum > m.getMaxArgs() || argsNum < m.getMinArgs()) {
			sender.sendMessage(help);
			return false;
		}
		if (!sender.hasPermission("coinflipper." + m.getPermission().toLowerCase())) {
			sender.sendMessage(Message.NO_PERMISSION.getMessage());
			return false;
		}
		
		
		if (m.isPlayerOnly() && !senderPlayer) {
			sender.sendMessage(Message.CMD_PLAYER_ONLY.getMessage());
			return false;
		}
		
		if (m.isPlayerOnly()) {
			Player p = (Player)sender;
			boolean enabledInWorld = true;
			for (String world : ConfigVar.DISABLED_WORLDS.getString().split(",")){
				if (p.getWorld().getName().equals(world)){
					enabledInWorld = false;
				}
			}
			
			if (!enabledInWorld){
				p.sendMessage(Message.DISABLED_IN_WORLD.getMessage());
				return false;
			}
		}
		
		return true;
	}
}
