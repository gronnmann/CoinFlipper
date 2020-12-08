package io.github.gronnmann.coinflipper.stats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.SQLManager;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.sql.coinflipper.SQLite;

public class StatsManager implements Listener{
	private StatsManager(){}
	private static StatsManager mng = new StatsManager();
	public static StatsManager getManager(){return mng;}
	
	
	protected HashMap<String, Stats> stats = new HashMap<String, Stats>();
	
	
	//Called when plugin is enabled to fetch all stats
	public void load(){
		
		for (Player oPl : Bukkit.getOnlinePlayers()){
			loadStats(oPl.getUniqueId().toString());
		}
	}
	
	
	//Called when plugin is disabled to save all stats
	
	
	private void loadStats(String uuid) {
		new BukkitRunnable() {
			
			@Override
			public void run(){
				try{	
				
				Connection conn = SQLManager.getManager().getSQLConnection();
				Debug.print("Fetching stats for: " + uuid);
					
				PreparedStatement getStats = conn.prepareStatement(
							"SELECT "
							+ "SUM(CASE WHEN winner=? THEN 1 ELSE 0 END) as gamesWon,"
							+ "SUM(CASE WHEN loser=? THEN 1 ELSE 0 END) as gamesLost,"
							+ "SUM(CASE WHEN winner=? THEN moneyWon ELSE 0 END) as moneyWon,"
							+ "SUM(moneyPot) as moneySpent "
							+ "FROM coinflipper_history WHERE ? in (winner, loser)"
							);	
				getStats.setString(1, uuid);
				getStats.setString(2, uuid);
				getStats.setString(3, uuid);
				getStats.setString(4, uuid);
				ResultSet res = getStats.executeQuery();
				
					
				if (!res.next()){
					createClearStats(uuid);
					return;
				}
					
				Stats loadedStats = new Stats(
						res.getInt("gamesWon"),
						res.getInt("gamesLost"),
						res.getDouble("moneySpent")/2,
						res.getDouble("moneyWon"));
					
				setStats(uuid, loadedStats);
				
				
				res.close();
				
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			}.runTaskAsynchronously(CoinFlipper.getMain());
	}
	
	
	
	
	public void setStats(String uuid, Stats s){
		if (stats.containsKey(uuid)){
			stats.remove(uuid);
		}
		stats.put(uuid, s);
	}
	
	
	public Stats getStats(Player p){
		return getStats(p.getUniqueId().toString());	
	}
	public Stats getStats(String uuid){
		try{
			if (stats.containsKey(uuid))return stats.get(uuid);
				loadStats(uuid);
		}catch(Exception e){
			this.createClearStats(uuid);
		}
		return stats.get(uuid);
	}
	
	@EventHandler
	public void createStatsIfNew(PlayerJoinEvent e){
		if (!stats.containsKey(e.getPlayer().getUniqueId().toString())){	
			loadStats(e.getPlayer().getUniqueId().toString());
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
	
	@EventHandler
	public void stopMemoryLeaks(PlayerQuitEvent e) {
		String uuid = e.getPlayer().getUniqueId().toString();
		//check if player still online after 1 min (RELOG) - if not - remove from stats
		new StatsLeakCleaner(e.getPlayer().getName(), uuid).runTaskLaterAsynchronously(CoinFlipper.getMain(), 60*20);
		
	}
}


class StatsLeakCleaner extends BukkitRunnable{
	
	String name, uuid;
	
	public StatsLeakCleaner(String name, String uuid) {
		this.name = name;
		this.uuid = uuid;
	}
	
	public void run() {
		if (Bukkit.getPlayer(name) == null) {
			StatsManager.getManager().stats.remove(uuid);
			Debug.print("Removed memory leak: " + name);
		}
	}
}
