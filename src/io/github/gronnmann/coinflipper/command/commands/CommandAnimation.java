package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.animations.AnimationGUI;
import io.github.gronnmann.coinflipper.command.CommandModule;

public class CommandAnimation extends CommandModule{

	public CommandAnimation(String[] label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		AnimationGUI.getManager().openGUI(((Player)sender));
		
	}

}
