package io.github.gronnmann.utils.coinflipper;

import java.io.File;
import java.net.URL;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;


public class VersionUtils {
	
	
	public static int FETCH_FAILED = 0, VERSION_NEWER = 1, VERSION_SAME = 2, VERSION_OLDER = 3;
	
	public static int versionFromGithub(Plugin pl, String urlToPluginYml){
		try{
			File temp = File.createTempFile("tempVerCheck", ".yml");
		
			URL messagesOrginal = new URL(urlToPluginYml);
			FileUtils.copyURLToFile(messagesOrginal, temp);
		
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
}
