package io.github.gronnmann.coinflipper.animations;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.ConfigManager;

public class AnimationFileManager {
	private AnimationFileManager(){}
	private static AnimationFileManager fm = new AnimationFileManager();
	public static AnimationFileManager getManager(){
		return fm;
	}
	
	File animationFolder;
	
	
	public void setup(Plugin p){
		
		boolean creation = false;
		
		animationFolder = new File(p.getDataFolder(), "animations");
		if (!animationFolder.exists()){
			if (!animationFolder.mkdir()){
				System.out.println("[CoinFlipper] Failed to create animations folder. Disabling...");
				Bukkit.getPluginManager().disablePlugin(p);
			}
			creation = true;
		}
		
		if (creation){
			File defaultAnim = new File(animationFolder, "default.yml");
			FileConfiguration animation = YamlConfiguration.loadConfiguration(defaultAnim);
			ConfigManager.getManager().copyDefaults(animation, "/defaultAnim.yml");
			
			AnimationsManager.getManager().loadAnimation(animation, defaultAnim);
		}
		
		for (File animationF : animationFolder.listFiles()){
			FileConfiguration animation = YamlConfiguration.loadConfiguration(animationF);
			AnimationsManager.getManager().loadAnimation(animation, animationF);
		}
	}
	
	public File getAnimationFolder(){
		return animationFolder;
	}
	
	public FileConfiguration getAnimationFile(String str){
		File animFile = new File(animationFolder, str+".yml");
		
		return YamlConfiguration.loadConfiguration(animFile);
	}
	public File getFile(String str){
		File animFile = new File(animationFolder, str+".yml");
		return animFile;
	}
}
