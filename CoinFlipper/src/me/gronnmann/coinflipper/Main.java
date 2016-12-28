package me.gronnmann.coinflipper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;


public class Main extends JavaPlugin{
	
	private static Economy economy;
	
	
	public void onEnable(){
		
		if (!enableEconomy()){
			System.out.println("[CoinFlipper] Vault not found.");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		Bukkit.getPluginManager().registerEvents(GUI.getInstance(), this);
		
		this.getCommand("coinflipper").setExecutor(new CommandsManager());
		
		ConfigManager.getManager().setup(this);
		GUI.getInstance().setup(this);
		
		BettingTimer task = new BettingTimer();
		task.runTaskTimerAsynchronously(this, 0, 60*20);
		
	}
	
	public void onDisable(){
		/*if (BettingManager.getManager().getBets().isEmpty())return;
		for (Bet b : BettingManager.getManager().getBets()){
			economy.depositPlayer(b.getPlayer(), b.getAmount());
		}*/
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
