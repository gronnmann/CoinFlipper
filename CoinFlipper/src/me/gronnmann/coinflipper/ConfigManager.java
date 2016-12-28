package me.gronnmann.coinflipper;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	private ConfigManager(){}
	private static ConfigManager mng = new ConfigManager();
	public static ConfigManager getManager(){return mng;}
	
	private File configF;
	private FileConfiguration config;
	
	public void setup(Plugin p){
		configF = new File(p.getDataFolder(), "config.yml");
		if (!configF.exists()){
			p.saveDefaultConfig();
		}
		
		config = YamlConfiguration.loadConfiguration(configF);
		
		this.saveConfig();
	}
	
	
	public FileConfiguration getConfig(){return config;}
	
	public void saveConfig(){
		try{
			config.save(configF);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
