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

import io.github.gronnmann.utils.ItemUtils;


public class Animation {
	
	
	private FileConfiguration animationFile;
	private File file;
	
	public Animation(FileConfiguration animationFile, File file){
		this.animationFile = animationFile;
		this.file = file;
	}
	public ArrayList<Inventory> animationInventory = new ArrayList<Inventory>();
	
	public void copy(Animation copyInto){
		copyInto.animationInventory = (ArrayList<Inventory>) this.animationInventory.clone();
	}
	
	public void draw(){
		//Draw for every frame
		if (animationFile.getString("animation") == null){
			for (int frame = 0; frame <= 50; frame++){
				Inventory frameInv = Bukkit.createInventory(null, 45);
				animationInventory.add(frame, frameInv);
			}
			return;
		}
		
		for (int frame = 0; frame <= 50; frame++){
			Inventory frameInv = Bukkit.createInventory(null, 45);
			//For every slot
			for (int slot = 0; slot <= 44; slot++){
				Material forSlot = null;
				
				try{
					forSlot = Material.valueOf(animationFile.getString("animation." + frame + "."+slot + ".material"));
				}catch(Exception e){
					forSlot = Material.AIR;
				}
				
				
				short data = (short)animationFile.getInt("animation." + frame + "."+slot + ".data");
				ItemStack item = new ItemStack(forSlot, 1, (short)data);
				
				if (item.getType() != Material.AIR){
					ItemUtils.setName(item, animationFile.getString("animation." + frame + "."+slot + ".name"));
				}
				
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
				if (item != null){
					animationFile.set("animation."+frame+"."+slot+".material", item.getType().toString());
					animationFile.set("animation."+frame+"."+slot+".data", item.getData().getData());
					if (item.getItemMeta() != null){
						animationFile.set("animation."+frame+"."+slot+".name", item.getItemMeta().getDisplayName());
					}else{
						animationFile.set("animation."+frame+"."+slot+".name", " ");
					}
				}
				
				
			}
		}
		
		try {
			animationFile.save(file);
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
	public Inventory getFrame(int frame){
		return animationInventory.get(frame);
	}
	
	public String getName(){
		String name = file.getName();
		name = name.substring(0, name.length()-4);
		return name;
	}
	
	public File getFile(){
		return file;
	}
}
