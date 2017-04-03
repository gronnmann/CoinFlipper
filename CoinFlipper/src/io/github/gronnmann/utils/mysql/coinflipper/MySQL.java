package io.github.gronnmann.utils.mysql.coinflipper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQL {
	
	private boolean connected;
	
	private Connection connection;
	
	private String ip, database, user, password;
	
	public MySQL(String ip, String database, String user, String password){
		
		this.ip = ip;
		this.database = database;
		this.user = user;
		this.password = password;
		
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			
			
			connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + database + "?user=" + user + "&password=" + password);
			
			connected = true;
		}catch(Exception e){
			connected = false;
			e.printStackTrace();
		}
		
		
		
	}
	
	public String getServerIP(){
		return ip;
	}
	public String getDatabase(){
		return database;
	}
	public String getUsername(){
		return user;
	}
	public String getPassword(){
		return password;
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
