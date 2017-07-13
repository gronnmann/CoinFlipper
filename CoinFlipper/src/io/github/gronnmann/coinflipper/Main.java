package io.github.gronnmann.coinflipper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.gronnmann.coinflipper.animations.AnimationFileManager;
import io.github.gronnmann.coinflipper.animations.AnimationGUI;
import io.github.gronnmann.coinflipper.animations.AnimationsManager;
import io.github.gronnmann.coinflipper.bets.BettingManager;
import io.github.gronnmann.coinflipper.bets.BettingTimer;
import io.github.gronnmann.coinflipper.gui.CreationGUI;
import io.github.gronnmann.coinflipper.gui.SelectionScreen;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.metrics.BStats;
import io.github.gronnmann.coinflipper.mysql.SQLManager;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.coinflipper.VersionUtils;
import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin{
	
	private static Economy economy;
	
	private static Main main;
	
	
	
	public void onEnable(){
		
		main = this;
		
		if (!enableEconomy()){
			
			int i = 0;
			while (i < 4){
				i++;
				System.out.println("[CoinFlipper] Vault/Economy plugins not found. Disabling.");
			}
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		ConfigManager.getManager().setup(this);
		ConfigManager.getManager().configUpdate();
		
		MaterialsManager.setup(this);
		
		SelectionScreen.getInstance().setup(this);
		StatsManager.getManager().load();
		
		AnimationFileManager.getManager().setup(this);
		AnimationGUI.getManager().setup();
		
		BettingManager.getManager().load();
		
		HookManager.getManager().registerHooks(this);
		
		
		CreationGUI.getInstance().generatePreset();
		
		this.getCommand("coinflipper").setExecutor(new CommandsManager());
		
		
		BettingTimer task = new BettingTimer();
		task.runTaskTimerAsynchronously(this, 0, 60*20);
		
		
		Bukkit.getPluginManager().registerEvents(SelectionScreen.getInstance(), this);
		Bukkit.getPluginManager().registerEvents(CreationGUI.getInstance(), this);
		
		Bukkit.getPluginManager().registerEvents(StatsManager.getManager(), this);
		
		
		Bukkit.getPluginManager().registerEvents(AnimationGUI.getManager(), this);
		
		int versionResponse = VersionUtils.versionFromGithub(this, "https://raw.githubusercontent.com/gronnmann/CoinFlipper/master/CoinFlipper/src/plugin.yml");
		
		if (versionResponse == VersionUtils.VERSION_NEWER){
			System.out.println("[CoinFlipper] You are running an experimental version of CoinFlipper.");
			System.out.println("[CoinFlipper] Expect bugs");
		}else if (versionResponse == VersionUtils.VERSION_OLDER){
			System.out.println("[CoinFlipper] You are using an old CoinFlipper version. Please update for best stability at:");
			System.out.println("[CoinFlipper] https://www.spigotmc.org/resources/coinflipper.33916/");
		}
		
		SQLManager.getManager().setup();
		
		
		//Start metrics
		new BStats(this);
		
	}
	
	public void onDisable(){
		StatsManager.getManager().save();
		BettingManager.getManager().save();
		
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
	
	public static Main getMain(){
		return main;
	}
}
