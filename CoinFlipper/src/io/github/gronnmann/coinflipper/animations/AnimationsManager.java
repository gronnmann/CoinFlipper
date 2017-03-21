package io.github.gronnmann.coinflipper.animations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.utils.Debug;

public class AnimationsManager{
	private AnimationsManager(){}
	private static AnimationsManager mng = new AnimationsManager();
	public static AnimationsManager getManager(){
		return mng;
	}
	private ArrayList<Animation> animations = new ArrayList<Animation>();
	
	public void save(){
		for (Animation ani : animations){
			ani.save();
			
			Debug.print("Saving animation: " + ani.getName());
		}
	}
	
	public Animation createAnimation(String name){
		File animationF = new File(AnimationFileManager.getManager().getAnimationFolder(), name+".yml");
		try {
			animationF.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileConfiguration animation = YamlConfiguration.loadConfiguration(animationF);
		
		Animation anim = new Animation(animation, animationF);
		anim.draw();
		
		animations.add(anim);
		return anim;
	}
	public void removeAnimation(String name){
		Animation anim = this.getAnimation(name);
		anim.getFile().delete();
		animations.remove(anim);
	}
	
	public void removeAnimation(Animation anim){
		anim.getFile().delete();
		animations.remove(anim);
	}
	
	public Animation loadAnimation(FileConfiguration config, File file){
		Animation animation = new Animation(config, file);
		animation.draw();
		animations.add(animation);
		
		return animation;
	}
	
	public Animation getAnimation(String name){
		for (Animation a: animations){
			if (a.getName().equals(name)){
				return a;
			}
		}
		return null;
	}
	public ArrayList<Animation> getAnimations(){
		return animations;
	}
	
	public Animation getDefault(){
		for (Animation anim : animations){
			if (anim.isDefault()){
				return anim;
			}
		}
		return null;
	}
	
	public void setDefault(Animation anim){
		for (Animation a : animations){
			a.setDefault(false);
		}
		anim.setDefault(true);
		
		ConfigManager.getManager().getConfig().set("animation_default", anim.getName());
		ConfigManager.getManager().saveConfig();
	}
	
	public Animation getAnimationToUse(Player p){
		Animation animUsed = this.getDefault();
		
		
		for (Animation anim : animations){
			if (p.hasPermission("coinflipper.animations."+ anim.getName()) ){
				animUsed = anim;
			}
		}
		
		return animUsed;
		
	}
}
