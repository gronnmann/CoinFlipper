package io.github.gronnmann.utils.coinflipper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class VersionUtils {
	
	
	public static int FETCH_FAILED = 0, VERSION_NEWER = 1, VERSION_SAME = 2, VERSION_OLDER = 3;
	
	public static int versionFromGithub(Plugin pl, String urlToPluginYml){
		try{
			File temp = File.createTempFile("tempVerCheck", ".yml");
		
			URL messagesOrginal = new URL(urlToPluginYml);
			copyURLToFile(messagesOrginal, temp);
		
			FileConfiguration pluginyml = YamlConfiguration.loadConfiguration(temp);
			
			double nowV = Double.parseDouble(pl.getDescription().getVersion());
			double urlV = pluginyml.getDouble("version");
			
			Debug.print("Current version: " + nowV + ", Newest available: " + urlV);
			
			temp.delete();
			
			if (nowV < urlV){
				return VERSION_OLDER;
			}else if (nowV > urlV){
				return VERSION_NEWER;
			}else{
				return VERSION_SAME;
			}
			
			
			
		}catch(Exception e){
			return FETCH_FAILED;
		}
		
		
		
		
	}
	
	
	private static void copyURLToFile(URL url, File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			
			int c;
			while ((c = reader.read()) != -1) {
				writer.write(((char)c));
			}
			reader.close();
			writer.close();
		}catch(Exception e) {
			Debug.print("Failed reading URL " + url.toString() + " to file.");
		}
	}
	
}
