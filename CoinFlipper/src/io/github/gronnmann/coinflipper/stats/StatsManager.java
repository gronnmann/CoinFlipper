package io.github.gronnmann.coinflipper.stats;

import java.util.HashMap;

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
	
	//Called when plugin is enabled to fetch all stats
	public void load(){
		statsC = ConfigManager.getManager().getStats();
		
		if (statsC.getConfigurationSection("stats") == null)return;
		for (String allStats : statsC.getConfigurationSection("stats").getKeys(false)){
			int gamesWon = statsC.getInt("stats." + allStats + ".gamesWon");
			int gamesLost = statsC.getInt("stats." + allStats + ".gamesLost");
			double moneyUsed = statsC.getDouble("stats." + allStats + ".moneySpent");
			double moneyWon = statsC.getDouble("stats." + allStats + ".moneyWon");
			Stats statsS = new Stats(gamesWon, gamesLost, moneyUsed, moneyWon);
			
			stats.put(allStats, statsS);
			
			
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
			this.createClearStats(p);
		}
		
		return stats.get(p.getUniqueId().toString());
	}
	
	public void setStats(String uuid, Stats s){
		stats.put(uuid, s);
	}
	
	public Stats getStats(String uuid){
		if (!stats.containsKey(uuid)){
			this.createClearStats(uuid);
		}
		
		return stats.get(uuid);
	}
	
	@EventHandler
	public void createStatsIfNew(PlayerJoinEvent e){
		if (!stats.containsKey(e.getPlayer().getUniqueId().toString())){
			
			if (SQLManager.getManager().isEnabled()){
				SQLManager.getManager().loadStats(e.getPlayer().getUniqueId().toString());
			}else{
				this.createClearStats(e.getPlayer());
			}
		}
	}
	
	private void createClearStats(Player p){
		if (!stats.containsKey(p.getUniqueId().toString())){
			Stats clean = new Stats(0, 0, 0, 0);
			stats.put(p.getUniqueId().toString(),clean);
		}
	}
	
	private void createClearStats(String uuid){
		if (!stats.containsKey(uuid)){
			Stats clean = new Stats(0, 0, 0, 0);
			stats.put(uuid ,clean);
		}
	}
}
