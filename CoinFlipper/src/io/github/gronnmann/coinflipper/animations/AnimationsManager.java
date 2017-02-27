package io.github.gronnmann.coinflipper.animations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

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
	
	public void loadAnimation(FileConfiguration config, File file){
		Animation animation = new Animation(config, file);
		animation.draw();
		animations.add(animation);
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
}
