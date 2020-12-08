package io.github.gronnmann.coinflipper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.animations.AnimationFileManager;
import io.github.gronnmann.coinflipper.animations.AnimationGUI;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.CustomMaterial;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.gui.CreationGUI;
import io.github.gronnmann.coinflipper.gui.SelectionScreen;
import io.github.gronnmann.coinflipper.gui.configurationeditor.config.ConfigEditor;
import io.github.gronnmann.utils.coinflipper.ConfigurationFile;

public class ConfigManager {
	private ConfigManager(){}
	private static ConfigManager mng = new ConfigManager();
	public static ConfigManager getManager(){return mng;}
	
	private Plugin pl;
	
	
	
	private ConfigurationFile config, messages, bets, mysql, materials;
	
	public void setup(){
		
		this.pl = CoinFlipper.getMain();
		
		if (!pl.getDataFolder().exists()) {
			pl.getDataFolder().mkdirs();
		}
		
		//Config
		config = new ConfigurationFile("config.yml");
		for (ConfigVar var : ConfigVar.values()) {
			var.load();
		}
		
		//Messages
		messages = new ConfigurationFile("messages.yml");
		for (Message msg : Message.values()) {
			msg.load();
		}
		
		
		bets = new ConfigurationFile("bets.yml");
		
		mysql = new ConfigurationFile("mysql.yml", true);
		
		materials = new ConfigurationFile("materials.yml");
		for (CustomMaterial m : CustomMaterial.values()) {
			m.load();
		}
		
	}
	
	
	public FileConfiguration getConfig(){return config.getConfig();}
	public FileConfiguration getMessages(){return messages.getConfig();}
	public FileConfiguration getBets() {return bets.getConfig();}
	public FileConfiguration getMySQL(){return mysql.getConfig();}
	public FileConfiguration getMaterials(){return materials.getConfig();}
	
	public boolean saveConfig(){
		if (config.isLoaded()) {
			return config.save();
		}
		return false;
	}
	public void saveMessages(){
		messages.save();
	}
	public void saveBets(){
		bets.save();
	}
	public void saveMySQL(){
		mysql.save();
	}
	public void saveMaterials(){
		materials.save();
	}
	
	public void reload(){
		
		
		setup();
		//SQLManager.getManager().reload();
		SelectionScreen.getInstance().setup();
		AnimationFileManager.getManager().setup();
		AnimationGUI.getManager().setup();
		BettingManager.getManager().load();
		CreationGUI.getInstance().generatePreset();
		ConfigEditor.getInstance().setup();
	}
}
