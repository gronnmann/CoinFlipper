package io.github.gronnmann.coinflipper;


import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.stats.Stats;
import io.github.gronnmann.coinflipper.stats.StatsManager;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.sql.coinflipper.MySQL;
import io.github.gronnmann.utils.sql.coinflipper.SQL;
import io.github.gronnmann.utils.sql.coinflipper.SQLite;

public class SQLManager {
	private SQLManager(){}
	private static SQLManager mng = new SQLManager();
	public static SQLManager getManager(){
		return mng;
	}
	
	
	private SQL sql;
	
	public static String convertedUUID = "00000000-0000-0000-0000-000000000000";
	
	private Connection conn;
	private boolean enabled;
	
	public void setup(){
		
		enabled = ConfigVar.MYSQL_ENABLED.getBoolean();
		
		if (!enabled) {
			System.out.println("[CoinFlipper] Connecting to SQLite...");
			sql = new SQLite(CoinFlipper.getMain(), "coinflipper");
			
			
		}else {
			System.out.println("[CoinFlipper] Connecting to MySQL...");
			
			FileConfiguration conf = ConfigManager.getManager().getMySQL();
			
			sql = new MySQL(conf.getString("server"),
					conf.getString("database"),
					conf.getString("user"),
					conf.getString("password"));
		}
		
		if (!sql.isConnected())return;
		
		conn = sql.getConnection();
		
		if (!sql.tableExists("coinflipper_history")) {
			try {
				
				String autoIncr = (sql instanceof SQLite) ? "AUTOINCREMENT" : "AUTO_INCREMENT";
				String dateLong = (sql instanceof SQLite) ? "INT" : "BIGINT";
				
				PreparedStatement createGamesTable = conn.prepareStatement(
						"CREATE TABLE coinflipper_history("
						+ "id INTEGER PRIMARY KEY " + autoIncr + ","
						+ "time " + dateLong + " NOT NULL,"
						+ "winner CHAR(36),"
						+ "loser CHAR(36) NOT NULL,"
						+ "moneyWon DOUBLE NOT NULL,"
						+ "moneyPot DOUBLE NOT NULL,"
						+ "tax DOUBLE NOT NULL"
						+ ")"
						);
				
				createGamesTable.execute();
				
				
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
		if (sql.tableExists("coinflipper_stats")) {
			convertOld(false);
		}
		//try converting old if exists
		File oldFile = new File(CoinFlipper.getMain().getDataFolder(), "stats.db");
		if (oldFile.exists())convertOld(true);
		
	}
	
	private void convertOld(boolean sqlite) {
		System.out.println("[CoinFlipper] Converting old stats database. This may take some minutes...");
		Connection oldC = null;
		
		if (sqlite) {
			SQL old = new SQLite(CoinFlipper.getMain(), "stats");
			oldC = old.getConnection();
			if (!old.isConnected()) {
				System.out.println("[CoinFlipper] Problem reading old database. Stopping conversion.");
				return;
			}
		}else {
			oldC = conn;
		}
		
		
		try {
			PreparedStatement getAllOldStats = oldC.prepareStatement("SELECT * FROM coinflipper_stats");
			ResultSet oldData = getAllOldStats.executeQuery();
			
			while(oldData.next()) {
				
				String convertStatement = "INSERT INTO coinflipper_history(time, winner, loser, moneyWon, moneyPot,tax)"
						+ "VALUES (?,?,?,?,?,?)";
				
				PreparedStatement newS1 = conn.prepareStatement(convertStatement);
				
				//Add the right values for money fields
				newS1.setLong(1, System.currentTimeMillis());
				newS1.setString(2, oldData.getString("uuid"));
				newS1.setString(3, convertedUUID);
				newS1.setDouble(4, oldData.getDouble("moneyWon"));
				newS1.setDouble(5, oldData.getDouble("moneySpent")*2);
				newS1.setDouble(6, 0);
				
				newS1.execute();
				
				
				//Add correct amount of wins/lossers
				int wins = oldData.getInt("gamesWon")-1;
				int losses = oldData.getInt("gamesLost");
				
				for (int i = 0; i < wins; i++) {
					PreparedStatement newS2 = conn.prepareStatement(convertStatement);
					newS2.setLong(1, System.currentTimeMillis());
					newS2.setString(2, oldData.getString("uuid"));
					newS2.setString(3, convertedUUID);
					newS2.setDouble(4, 0);
					newS2.setDouble(5, 0);
					newS2.setDouble(6, 0);
					newS2.execute();
				}
				
				for (int i = 0; i < losses; i++) {
					PreparedStatement newS2 = conn.prepareStatement(convertStatement);
					newS2.setLong(1, System.currentTimeMillis());
					newS2.setString(2, convertedUUID);
					newS2.setString(3, oldData.getString("uuid"));
					newS2.setDouble(4, 0);
					newS2.setDouble(5, 0);
					newS2.setDouble(6, 0);
					newS2.execute();
				}
				
				Debug.print("Converting for " + oldData.getString("uuid"));
				
			}
			
			if (sqlite) {
				oldC.close();
				File oldDb = new File(CoinFlipper.getMain().getDataFolder(), "stats.db");
				oldDb.renameTo(new File(CoinFlipper.getMain().getDataFolder(), "stats.db.old"));
				System.out.println("[CoinFlipper] Conversion complete. Old stats file still available as stats.old.db");
			}else {
				conn.prepareStatement("RENAME TABLE coinflipper_stats TO coinflipper_stats_old").execute();
				System.out.println("[CoinFlipper] Conversion complete. Old stats table still available as coinflipper_stats_old");
			}
			
			
			
			
			
		}catch(Exception e) {
			System.out.println("[CoinFlipper] Problem converting old database. Error: ");
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public void reload() {
		try {
			conn.close();
			setup();
			System.out.println("[CoinFlipper] Reload of database complete.");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[CoinFlipper] Reload of database failed.");
		}
		
	}
	
	
	
	public Connection getSQLConnection(){
		return conn;
	}
	public SQL getSQL(){
		return sql;
	}
	
	/*public boolean isEnabled(){
		//Little workaround for SQLite to always work.
		return true;
		//return enabled;
	}*/
	
}
