package io.github.gronnmann.coinflipper.stats;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.mysql.SQLManager;
import io.github.gronnmann.utils.coinflipper.Debug;

public class StatsManager implements Listener{
	private StatsManager(){}
	private static StatsManager mng = new StatsManager();
	public static StatsManager getManager(){return mng;}
	
	
	private HashMap<String, Stats> stats = new HashMap<String, Stats>();
	private FileConfiguration statsC;
	
	//STILL INCLUDES METHOD BEFORE SQLITE WAS ADDED
	
	//Called when plugin is enabled to fetch all stats
	public void load(){
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			SQLManager.getManager().loadStats(oPl.getUniqueId().toString());
		}
	}
	
	
	//Called when plugin is disabled to save all stats
	public void save(){
		for (String players : stats.keySet()){
			
			SQLManager.getManager().saveStats(players, stats.get(players));
			
		}
	}
	
	public Stats getStats(Player p){
		if (!stats.containsKey(p.getUniqueId().toString())){
			try{
				if (SQLManager.getManager().isEnabled()){
					SQLManager.getManager().loadStats(p.getUniqueId().toString());
				}else{
					int gamesWon = statsC.getInt("stats." + p.getUniqueId().toString() + ".gamesWon");
					int gamesLost = statsC.getInt("stats." + p.getUniqueId().toString() + ".gamesLost");
					double moneyUsed = statsC.getDouble("stats." + p.getUniqueId().toString() + ".moneySpent");
					double moneyWon = statsC.getDouble("stats." + p.getUniqueId().toString() + ".moneyWon");
					Stats statsS = new Stats(gamesWon, gamesLost, moneyUsed, moneyWon);
					
					stats.put(p.getUniqueId().toString(), statsS);
				}
			}catch(Exception e){
				this.createClearStats(p);
			}
		}
		
		return stats.get(p.getUniqueId().toString());
	}
	
	public void setStats(String uuid, Stats s){
		if (stats.containsKey(uuid)){
			stats.remove(uuid);
		}
		stats.put(uuid, s);
	}
	
	public Stats getStats(String uuid){
		try{
			if (SQLManager.getManager().isEnabled()){
				if (stats.containsKey(uuid))return stats.get(uuid);
				SQLManager.getManager().loadStats(uuid);
			}else{
				int gamesWon = statsC.getInt("stats." + uuid + ".gamesWon");
				int gamesLost = statsC.getInt("stats." + uuid + ".gamesLost");
				double moneyUsed = statsC.getDouble("stats." + uuid + ".moneySpent");
				double moneyWon = statsC.getDouble("stats." + uuid + ".moneyWon");
				Stats statsS = new Stats(gamesWon, gamesLost, moneyUsed, moneyWon);
				
				stats.put(uuid, statsS);
			}
		}catch(Exception e){
			this.createClearStats(uuid);
		}
		return stats.get(uuid);
	}
	
	@EventHandler
	public void createStatsIfNew(PlayerJoinEvent e){
		if (!stats.containsKey(e.getPlayer().getUniqueId().toString())){	
			SQLManager.getManager().loadStats(e.getPlayer().getUniqueId().toString());
		}
	}
	
	public void createClearStats(Player p){
		if (!stats.containsKey(p.getUniqueId().toString())){
			Debug.print("Creating new stats for " + p.getName());
			Stats clean = new Stats(0, 0, 0, 0);
			stats.put(p.getUniqueId().toString(),clean);
		}
	}
	
	public void createClearStats(String uuid){
		if (!stats.containsKey(uuid)){
			Debug.print("Creating new stats for " + uuid);
			Stats clean = new Stats(0, 0, 0, 0);
			stats.put(uuid ,clean);
		}
	}
	
	public boolean convertToSQLite(){
		if (statsC == null || statsC.getConfigurationSection("stats").getKeys(false) == null)return false;
		try{
			for (String allStats : statsC.getConfigurationSection("stats").getKeys(false)){
				int gamesWon = statsC.getInt("stats." + allStats + ".gamesWon");
				int gamesLost = statsC.getInt("stats." + allStats + ".gamesLost");
				double moneyUsed = statsC.getDouble("stats." + allStats + ".moneySpent");
				double moneyWon = statsC.getDouble("stats." + allStats + ".moneyWon");
				Stats statsS = new Stats(gamesWon, gamesLost, moneyUsed, moneyWon);
				
				Debug.print("Converting: " + allStats);
				
				SQLManager.getManager().saveStats(allStats, statsS);
			}
			
			
			stats.clear();
			
			for (Player oPl : Bukkit.getOnlinePlayers()){
				SQLManager.getManager().loadStats(oPl.getUniqueId().toString());
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		
		
	}
}
