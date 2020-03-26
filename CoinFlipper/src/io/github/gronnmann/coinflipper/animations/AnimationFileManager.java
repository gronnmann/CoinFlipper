package io.github.gronnmann.coinflipper.animations;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;

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
			AnimationsManager.getManager().recreateNewDefault();
		}
		
		int animCount = 0;
		
		for (File animationF : animationFolder.listFiles()){
			FileConfiguration animation = YamlConfiguration.loadConfiguration(animationF);
			Animation anim = AnimationsManager.getManager().loadAnimation(animation, animationF);
			animCount++;
			//Default setter
			if (anim.getName().equals(ConfigVar.ANIMATION_DEFAULT.getString())){
				AnimationsManager.getManager().setDefault(anim);
			}
		}
		
		if (animCount == 0){
			AnimationsManager.getManager().recreateNewDefault();
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
