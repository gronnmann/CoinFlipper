package io.github.gronnmann.utils.sql.coinflipper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

public class SQLite extends SQL{
	
	
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
	
	
}
