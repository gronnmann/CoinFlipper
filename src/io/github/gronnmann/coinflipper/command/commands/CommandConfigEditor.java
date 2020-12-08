package io.github.gronnmann.coinflipper.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.command.CommandModule;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.gui.configurationeditor.FileEditSelector;

public class CommandConfigEditor extends CommandModule{

	public CommandConfigEditor(String label, String permission, int minArgs, int maxArgs, boolean playerOnly) {
		super(label, permission, minArgs, maxArgs, playerOnly);
	}

	@Override
	public void runCommand(CommandSender sender, String[] args) {
		FileEditSelector.getInstance().openConfigurator(((Player)sender));
	}
	
}
