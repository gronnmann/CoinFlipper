package io.github.gronnmann.utils.coinflipper;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationVariable {
	
	private FileConfiguration config;
	private File configF;
	
	private JavaPlugin plugin;
	
	private String path;
	private Object value, defaultValue;
	
	private boolean initialized = false;
	
	
	public ConfigurationVariable(File config, JavaPlugin plugin, String path, Object defaultValue) {
		this.configF = config;
		this.plugin = plugin;
		
		this.path = path;
		this.value = defaultValue;
		this.defaultValue = defaultValue;
	}
	
	
	public void loadFile() {
		if (!configF.getParentFile().exists()) {
			configF.getParentFile().mkdirs();
		}
		if (!configF.exists()) {
			try {
				configF.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().info("Failed to create " + configF.getName());
				return;
			}
		}
		
		try {
			config = YamlConfiguration.loadConfiguration(configF);
			initialized = true;
		}catch(Exception e) {
			plugin.getLogger().info("Failed to load " + configF.getName() + " as config.");
		}
	}
	
	public Object getValue() {
		return value;
	}
	public String getString() {
		return String.valueOf(value);
	}
	public double getDouble() {
		return Double.parseDouble(getString());
	}
	public int getInt() {
		return Integer.parseInt(getString());
	}
	public boolean getBoolean() {
		return Boolean.valueOf(getString());
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	
	
	public void setValue(Object newValue) {
		this.value = newValue;
		this.save();
	}
	
	
	public void load() {
		if (!initialized)return;
		
		
		if (config.get(path) != null) {
			this.value = config.get(path);
			return;
		}
		
		save();
	}
	
	public void save() {
		if (!initialized)return;
		
		config.set(path, value);
		
		try {
			config.save(configF);
		}catch(Exception e) {
			plugin.getLogger().info("Failed to save cvar " + path + " (value " + getString() + ")");
		}
		
	}
	
	
}
