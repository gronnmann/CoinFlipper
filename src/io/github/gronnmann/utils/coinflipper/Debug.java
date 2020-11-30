package io.github.gronnmann.utils.coinflipper;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Debug {
	public static void print(String message){
		File debugEnabler = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath(), "debug.yml");
		if (!debugEnabler.exists())return;
		FileConfiguration utilsConfig = YamlConfiguration.loadConfiguration(debugEnabler);
		if (utilsConfig.getString("debug") == null)return;
		if (!utilsConfig.getBoolean("debug"))return;
		
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + message);
	}
}
