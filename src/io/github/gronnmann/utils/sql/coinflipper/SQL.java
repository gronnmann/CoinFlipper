package io.github.gronnmann.utils.sql.coinflipper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL {
	
	protected boolean connected = false;
	
	protected Connection connection;
	
	
	public SQL(){}
	
	
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
