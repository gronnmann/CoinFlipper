package io.github.gronnmann.coinflipper.animations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class Animation {
	
	
	private FileConfiguration animationFile;
	public Animation(FileConfiguration animationFile){
		this.animationFile = animationFile;
	}
	public ArrayList<Inventory> animationInventory = new ArrayList<Inventory>();
	
	public void draw(){
		//Draw for every frame
		for (int frame = 0; frame <= 50; frame++){
			Inventory frameInv = Bukkit.createInventory(null, 45);
			//For every slot
			for (int slot = 0; slot <= 44; slot++){
				Material forSlot = Material.valueOf(animationFile.getString("animation." + frame + "."+slot + ".material"));
				short data = (short)animationFile.getInt("animation." + frame + "."+slot + ".data");
				ItemStack item = new ItemStack(forSlot, 1, (short)data);
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(animationFile.getString("animation." + frame + "."+slot + ".name"));
				item.setItemMeta(meta);
				
				frameInv.setItem(slot, item);
			}
			animationInventory.add(frame, frameInv);
		}
	}
	
	public void save(){
		for (int frame = 0; frame <= 50; frame++){
			Inventory inv = animationInventory.get(frame);
			for (int slot = 0; slot <= 44; slot++){
				ItemStack item = inv.getItem(slot);
				animationFile.set("animation."+frame+"."+slot+".material", item.getType().toString());
				animationFile.set("animation."+frame+"."+slot+".data", item.getData().getData());
				if (item.getItemMeta() != null){
					animationFile.set("animation."+frame+"."+slot+".name", item.getItemMeta().getDisplayName());
				}else{
					animationFile.set("animation."+frame+"."+slot+".name", " ");
				}
				
			}
		}
		
		try {
			animationFile.save(new File(AnimationFileManager.getManager().getAnimationFolder(), animationFile.getName()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Inventory> getFrames(){
		return animationInventory;
	}
	public void setFrame(int frame, Inventory window){
		animationInventory.set(frame, window);
	}
	
	public String getName(){
		return animationFile.getName().split(".")[0];
	}
}
