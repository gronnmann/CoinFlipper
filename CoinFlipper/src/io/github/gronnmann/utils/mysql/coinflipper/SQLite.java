package io.github.gronnmann.utils.mysql.coinflipper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

public class SQLite {
	
	private boolean connected;
	
	private Connection connection;
	private File databaseFile;
	
	public SQLite(Plugin p, String dbName){
		
		if (!p.getDataFolder().exists()){
			p.getDataFolder().mkdirs();
		}
		
		
		databaseFile = new File(p.getDataFolder(), dbName+".db");
		if (!databaseFile.exists()){
			try {
				databaseFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try{
			
			Class.forName("org.sqlite.JDBC");
			
			
			connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
			
			connected = true;
		}catch(Exception e){
			connected = false;
			e.printStackTrace();
		}
		
		
		
	}
	
	public File getDatabaseFile(){
		return databaseFile;
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	public boolean tableExists(String table){
		table = table.toLowerCase();
		
		
		try {
			ResultSet tables = connection.getMetaData().getTables(null, null, table, new String[]{"TABLE"});
			
			return tables.next();
			
			
		} catch (SQLException e) {
			return false;
		}
	}
	
	
	
	
	
	
}
