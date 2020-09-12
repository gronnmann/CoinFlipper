package io.github.gronnmann.utils.coinflipper;

import java.io.File;

import javax.security.auth.login.Configuration;

import org.bukkit.configuration.file.YamlConfiguration;

import io.github.gronnmann.coinflipper.CoinFlipper;

public class ConfigurationFile {
	
	private File file;
	private YamlConfiguration yaml;
	private boolean loaded = false;
	
	public ConfigurationFile(String path) {
			this(path, false);
	}
	
	public ConfigurationFile(String path, boolean embedded) {
		CoinFlipper plugin = CoinFlipper.getMain();
		
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}
		
		this.file = new File(plugin.getDataFolder(), path);
		
		
		try {
			
			if (!file.exists()) {
				if (!embedded) {
					file.createNewFile();
				}else {
					CoinFlipper.getMain().saveResource(path, false);
				}
				System.out.println("[CoinFlipper] " + file.getName() + " not found. Creating new...");
			}
			
			yaml = YamlConfiguration.loadConfiguration(file);
			loaded = true;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("[CoinFlipper] Failed to load: " + file.getName());
		}
	}
	
	public File getConfigFile() {
		return file;
	}
	public YamlConfiguration getConfig() {
		return yaml;
	}
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean save() {
		try {
			yaml.save(file);
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("[CoinFlipper] Failed to save: " + file.getName());
			return false;
		}
	}
	
	
	
}
