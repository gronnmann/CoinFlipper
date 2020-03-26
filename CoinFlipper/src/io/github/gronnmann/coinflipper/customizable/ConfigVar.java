package io.github.gronnmann.coinflipper.customizable;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.ConfigManager;

public enum ConfigVar {
	MYSQL_ENABLED ("mysql_enabled", false, "Set to true to use MySQL instead of SQLite"),
	MIN_AMOUNT ("min_amount", 300, "Minimum money you can bet"),
	MAX_AMOUNT("max_amount", 1000000, "Maximum money you can bet"),
	TIME_EXPIRE ("time_expire", 60, "Time before bet expires in minutes"),
	TAX_PERCENTAGE ("tax_percentage", 5, "How high the tax on bets should be"),
	FORMATTING_SHORTEN_MONEY ("formatting_shorten_money", true, "Should numbers be shorted? Example: 1000000 will be displayed as 1M. If set to false, it will be displayed as 1,000,000."),
	SOUND_WHILE_CHOOSING ("sound_while_choosing", "CLICK", "What sound should play while flipping", "BLOCK_DISPENSER_DISPENSE", 9),
	SOUND_WINNER_CHOSEN ("sound_winner_chosen", "FIREWORK_BLAST", "What sound should play when a player wins" , "ENTITY_FIREWORK_BLAST", 9),
	FRAME_WINNER_CHOSEN ("frame_winner_chosen", 30, "At which frame should the winner be chosen. Useful for custom animations."),
	BOOSTERS_ENABLED ("boosters_enabled", false, "Should boosters be enabled"),
	ANIMATION_DEFAULT ("animation_default", "default", "Default animation for players"),
	BETS_PER_PLAYER ("bets_per_player", 1, "Maximum amount bets a player can place"),
	GUI_AUTO_CLOSE ("gui_auto_close", true, "Should the GUI close automatically after flip"),
	VALUE_NEEDED_TO_BROADCAST("value_needed_to_broadcast", 100000, "Value after which wins are broadcasted"),
	SIGN_INPUT ("sign_input", true,  "Should the SignInputAPI be enabled (requires ProtocolLib, MC 1.8-1.9)", false, 9),
	DISABLED_WORLDS ("disabled_worlds", "disabledWorld1,disabledWorld2", "At which worlds should the plugin be disabled"),
	ANIMATION_ONLY_CHALLENGER("animation_only_challenger", false, "Should the animation only play for the player who challenges the bet?");
	
	
	private String cvar, desc; 
	private Object value, defaultVal;
	
	
	private Object newVersionValue;
	private int whatVersion = 0;
	
	ConfigVar(String variable, Object value){
		this.cvar = variable;
		this.desc = "";
		
		this.defaultVal = value;
		this.value = value;
	}
	
	ConfigVar(String variable, Object value, String description){
		this.cvar = variable;
		this.desc = description;
		
		this.defaultVal = value;
		this.value = value;
	}
	
	ConfigVar(String variable, Object value, String description, Object newVersionValue, int whatVersion){
		this.cvar = variable;
		this.desc = description;
		
		this.defaultVal = value;
		this.value = value;
		
		this.newVersionValue = newVersionValue;
		this.whatVersion = whatVersion;
		
	}
	
	
	public String getPath() {
		return cvar;
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
	
	public String getDescription() {
		return desc;
	}
	
	public Object getDefaultValue() {
		return defaultVal;
	}
	
	
	public void setValue(Object newValue) {
		this.value = newValue;
		this.save(true);
	}
	
	
	
	public void save(boolean save) {
		FileConfiguration config = ConfigManager.getManager().getConfig();
		
		config.set(cvar, value);
		
		if (save) {
			ConfigManager.getManager().saveConfig();
		}
	}
	public void load() {
		
		
		FileConfiguration config = ConfigManager.getManager().getConfig();
		
		
		if (config == null)return;
		
		if (config.get(cvar) != null) {
			this.value = config.get(cvar);
			return;
		}
		
		
		if (newVersionValue != null) {
			if (CoinFlipper.versionId >= whatVersion) {
				value = newVersionValue;
			}
		}
		
		save(true);
	}
	
	public static ConfigVar fromPath(String path) {
		for (ConfigVar var : ConfigVar.values()) {
			if (var.getPath().equals(path))return var;
		}
		
		return null;
	}
}
