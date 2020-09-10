package io.github.gronnmann.coinflipper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
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
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.coinflipper.Debug;

public class ConfigManager {
	private ConfigManager(){}
	private static ConfigManager mng = new ConfigManager();
	public static ConfigManager getManager(){return mng;}
	
	private Plugin pl;
	private File configF, messagesF, betsF, mysqlF, materialsF;
	private FileConfiguration config, messages, bets, mysql, materials;
	
	public void setup(){
		
		this.pl = CoinFlipper.getMain();
		
		if (!pl.getDataFolder().exists()) {
			pl.getDataFolder().mkdirs();
		}
		
		//Config
		configF = new File(pl.getDataFolder(), "config.yml");
		if (!configF.exists())
			try {
				configF.createNewFile();
			} catch (IOException e1) {
				System.out.print("[CoinFlipper] Could not create config file.");
				e1.printStackTrace();
			}
		config = YamlConfiguration.loadConfiguration(configF);
		
		for (ConfigVar var : ConfigVar.values()) {
			var.load();
		}
		
		
		
		//Messages
		messagesF = new File(pl.getDataFolder(), "messages.yml");
		if (!messagesF.exists()){
			try{
				messagesF.createNewFile();
			}catch(Exception e){
				System.out.println("[CoinFlipper] Could not create messages.yml");
				e.printStackTrace();
			}
		}
		messages = YamlConfiguration.loadConfiguration(messagesF);
		for (Message msg : Message.values()) {
			msg.load();
		}
		
		
		betsF = new File(pl.getDataFolder(), "bets.yml");
		if (!betsF.exists()){
			try {
				betsF.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bets = YamlConfiguration.loadConfiguration(betsF);
		
		mysqlF = new File(pl.getDataFolder(), "mysql.yml");
		if (!mysqlF.exists()){
			try{
				//mysqlF.createNewFile();
				pl.saveResource("mysql.yml", false);
				mysql = YamlConfiguration.loadConfiguration(mysqlF);
				/*this.copyDefaults(mysql, "/mysql.yml");
				this.saveMySQL();*/
			}catch(Exception e){e.printStackTrace();}
		}else{
			mysql = YamlConfiguration.loadConfiguration(mysqlF);
		}
		
		materialsF = new File(pl.getDataFolder(), "materials.yml");
		if (!materialsF.exists()){
			try{
				materialsF.createNewFile();
			}catch(Exception e){
				System.out.print("[CoinFlipper] Could not create materials file.");
				e.printStackTrace();
			}
		}
		materials = YamlConfiguration.loadConfiguration(materialsF);
		for (CustomMaterial m : CustomMaterial.values()) {
			m.load();
		}
		
	}
	
	
	
	//Copy default option
	public void copyDefaults(FileConfiguration file, String resource){
		InputStream str;
		file.options().copyDefaults(true);
		try{
			str = pl.getClass().getResourceAsStream(resource);
			InputStreamReader strR = new InputStreamReader(str);
			FileConfiguration defaults = YamlConfiguration.loadConfiguration(strR);
			file.setDefaults(defaults);
			str.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public FileConfiguration getConfig(){return config;}
	public FileConfiguration getMessages(){return messages;}
	public FileConfiguration getBets() {return bets;}
	public FileConfiguration getMySQL(){return mysql;}
	public FileConfiguration getMaterials(){return materials;}
	
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
	public void saveBets(){
		try{
			bets.save(betsF);
		}catch(Exception e){e.printStackTrace();}
	}
	public void saveMySQL(){
		try{
			mysql.save(mysqlF);
		}catch(Exception e){e.printStackTrace();}
	}
	public void saveMaterials(){
		try{
			materials.save(materialsF);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void reload(){
		config = YamlConfiguration.loadConfiguration(configF);
		for (ConfigVar cvar : ConfigVar.values()) {
			cvar.load();
			cvar.save(true);
		}
		
		materials = YamlConfiguration.loadConfiguration(materialsF);
		for (CustomMaterial m : CustomMaterial.values()) {
			m.load();
			m.save(true, true);
		}
		
		messages = YamlConfiguration.loadConfiguration(messagesF);
		for (Message msg : Message.values()) {
			msg.load();
			msg.save();
		}
		
		BettingManager.getManager().save();
		
		
		bets = YamlConfiguration.loadConfiguration(betsF);
		mysql = YamlConfiguration.loadConfiguration(mysqlF);
		
		SelectionScreen.getInstance().setup(pl);
		AnimationFileManager.getManager().setup(pl);
		AnimationGUI.getManager().setup();
		BettingManager.getManager().load();
		CreationGUI.getInstance().generatePreset();
		ConfigEditor.getInstance().setup();
	}
}
