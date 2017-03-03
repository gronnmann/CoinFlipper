package io.github.gronnmann.coinflipper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.gronnmann.coinflipper.animations.AnimationFileManager;
import io.github.gronnmann.coinflipper.animations.AnimationGUI;
import io.github.gronnmann.coinflipper.animations.AnimationsManager;
import io.github.gronnmann.coinflipper.bets.BettingTimer;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin{
	
	private static Economy economy;
	
	
	public void onEnable(){
		
		if (!enableEconomy()){Bukkit.getPluginManager().disablePlugin(this);}
		
		ConfigManager.getManager().setup(this);
		GUI.getInstance().setup(this);
		StatsManager.getManager().load();
		
		AnimationFileManager.getManager().setup(this);
		AnimationGUI.getManager().setup();
		
		HookManager.getManager().registerHooks();
		
		this.getCommand("coinflipper").setExecutor(new CommandsManager());
		
		
		BettingTimer task = new BettingTimer();
		task.runTaskTimerAsynchronously(this, 0, 60*20);
		
		
		Bukkit.getPluginManager().registerEvents(GUI.getInstance(), this);
		Bukkit.getPluginManager().registerEvents(StatsManager.getManager(), this);
		
		///Bukkit.getPluginManager().registerEvents(AnimationsManager.getManager(), this);
		Bukkit.getPluginManager().registerEvents(AnimationGUI.getManager(), this);
		
	}
	
	public void onDisable(){
		StatsManager.getManager().save();
		
		AnimationsManager.getManager().save();
	}
	
	public boolean enableEconomy(){
		RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null){
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}
	
	
	public static Economy getEcomony(){
		return economy;
	}
}
