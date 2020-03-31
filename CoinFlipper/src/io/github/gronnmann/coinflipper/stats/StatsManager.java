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
				SQLManager.getManager().loadStats(p.getUniqueId().toString());
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
			if (stats.containsKey(uuid))return stats.get(uuid);
				SQLManager.getManager().loadStats(uuid);
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
}
