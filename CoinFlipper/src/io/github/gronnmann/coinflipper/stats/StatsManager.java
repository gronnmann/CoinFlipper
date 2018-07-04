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
		statsC = ConfigManager.getManager().getStats();
		
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			if (SQLManager.getManager().isEnabled()){
				SQLManager.getManager().loadStats(oPl.getUniqueId().toString());
			}else{
				int gamesWon = statsC.getInt("stats." + oPl.getUniqueId().toString() + ".gamesWon");
				int gamesLost = statsC.getInt("stats." + oPl.getUniqueId().toString() + ".gamesLost");
				double moneyUsed = statsC.getDouble("stats." + oPl.getUniqueId().toString() + ".moneySpent");
				double moneyWon = statsC.getDouble("stats." + oPl.getUniqueId().toString() + ".moneyWon");
				Stats statsS = new Stats(gamesWon, gamesLost, moneyUsed, moneyWon);
				
				stats.put(oPl.getUniqueId().toString(), statsS);
				this.createClearStats(oPl);
			}
		}
	}
	
	
	//Called when plugin is disabled to save all stats
	public void save(){
		for (String players : stats.keySet()){
			
			if (SQLManager.getManager().isEnabled()){
				SQLManager.getManager().saveStats(players, stats.get(players));
				
			}else{
				statsC.set("stats."+players+".gamesWon", stats.get(players).getGamesWon());
				statsC.set("stats."+players+".gamesLost", stats.get(players).getGamesLost());
				statsC.set("stats."+players+".moneySpent", stats.get(players).getMoneySpent());
				statsC.set("stats."+players+".moneyWon", stats.get(players).getMoneyWon());
				ConfigManager.getManager().saveStats();
			}
			
			Debug.print("Saving stats for: " + players);
			
		}
		ConfigManager.getManager().saveStats();
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
			
			try{
				if (SQLManager.getManager().isEnabled()){
					SQLManager.getManager().loadStats(e.getPlayer().getUniqueId().toString());
				}else{
					int gamesWon = statsC.getInt("stats." + e.getPlayer().getUniqueId().toString() + ".gamesWon");
					int gamesLost = statsC.getInt("stats." + e.getPlayer().getUniqueId().toString() + ".gamesLost");
					double moneyUsed = statsC.getDouble("stats." + e.getPlayer().getUniqueId().toString() + ".moneySpent");
					double moneyWon = statsC.getDouble("stats." + e.getPlayer().getUniqueId().toString() + ".moneyWon");
					Stats statsS = new Stats(gamesWon, gamesLost, moneyUsed, moneyWon);
					
					stats.put(e.getPlayer().getUniqueId().toString(), statsS);
				}
			}catch(Exception ex){
				this.createClearStats(e.getPlayer());
			}
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
		if (statsC == null)return false;
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
