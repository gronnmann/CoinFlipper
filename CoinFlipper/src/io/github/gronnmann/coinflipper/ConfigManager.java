package io.github.gronnmann.coinflipper;

import java.io.File;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.Plugin;

public class ConfigManager {
	private ConfigManager(){}
	private static ConfigManager mng = new ConfigManager();
	public static ConfigManager getManager(){return mng;}
	
	private Plugin pl;
	private File configF, messagesF, statsF, betsF;
	private FileConfiguration config, messages, stats, bets;
	
	public void setup(Plugin p){
		
		this.pl = p;
		
		//Config
		configF = new File(p.getDataFolder(), "config.yml");
		if (!configF.exists()){
			p.saveDefaultConfig();
			config = YamlConfiguration.loadConfiguration(configF);
			
			String packageName = Bukkit.getServer().getClass().getPackage().getName();
			int vID = Integer.parseInt(packageName.split("_")[1]);
			if (vID < 9){
				config.set("sound_while_choosing", "CLICK");
				config.set("sound_winner_chosen", "FIREWORK_BLAST");
			}
			
			
			this.saveConfig();
			
		}else{
			config = YamlConfiguration.loadConfiguration(configF);
		}
		
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		String vID = packageName.split("_")[1];
		
		
		
		//Messages
		messagesF = new File(p.getDataFolder(), "messages.yml");
		if (!messagesF.exists()){
			try{
				messagesF.createNewFile();
				messages = YamlConfiguration.loadConfiguration(messagesF);
				this.copyDefaults(messages, "/messages.yml");
				this.saveMessages();
			}catch(Exception e){e.printStackTrace();}
		}else{
			messages = YamlConfiguration.loadConfiguration(messagesF);
		}
		
		statsF = new File(p.getDataFolder(), "stats.yml");
		if (!statsF.exists()){
			try {
				statsF.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		stats = YamlConfiguration.loadConfiguration(statsF);
		
		betsF = new File(p.getDataFolder(), "bets.yml");
		if (!betsF.exists()){
			try {
				betsF.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bets = YamlConfiguration.loadConfiguration(betsF);
		
	}
	
	//Copy default option
	public void copyDefaults(FileConfiguration file, String resource){
		InputStream str;
		file.options().copyDefaults(true);
		try{
			str = pl.getClass().getResourceAsStream(resource);
			FileConfiguration defaults = YamlConfiguration.loadConfiguration(str);
			file.setDefaults(defaults);
			str.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public FileConfiguration getConfig(){return config;}
	public FileConfiguration getMessages(){return messages;}
	public FileConfiguration getStats(){return stats;}
	public FileConfiguration getBets() {return bets;}
	
	public void saveConfig(){
		try{
			config.save(configF);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void saveMessages(){
		try{
			messages.save(messagesF);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void saveStats(){
		try{
			stats.save(statsF);
		}catch(Exception e){e.printStackTrace();}
	}
	public void saveBets(){
		try{
			bets.save(betsF);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void reload(){
		config = YamlConfiguration.loadConfiguration(configF);
		messages = YamlConfiguration.loadConfiguration(messagesF);
		stats = YamlConfiguration.loadConfiguration(statsF);
		bets = YamlConfiguration.loadConfiguration(betsF);
	}
}
