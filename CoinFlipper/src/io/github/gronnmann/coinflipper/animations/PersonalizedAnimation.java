package io.github.gronnmann.coinflipper.animations;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;


public class PersonalizedAnimation extends Animation{

	private String p1, p2, winner;
	
	private ArrayList<Inventory> frames = new ArrayList<Inventory>();
	
	public PersonalizedAnimation(FileConfiguration animationFile, String p1, String p2, String winner) {
		super(animationFile);
		
		this.p1 = p1;
		this.p2 = p2;
		this.winner = winner;

		frames = (ArrayList<Inventory>) animationInventory.clone();
	}
	
	public void prepare(){
		ItemStack p1skull = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		ItemStack p2skull = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		SkullMeta p1sm = (SkullMeta)p1skull.getItemMeta();
		SkullMeta p2sm = (SkullMeta)p2skull.getItemMeta();
		p1sm.setDisplayName(ChatColor.AQUA + p1);
		p2sm.setDisplayName(ChatColor.AQUA + p2);
		p1skull.setItemMeta(p1sm);
		p2skull.setItemMeta(p2sm);
		
		
		ItemStack winSkull = new ItemStack(Material.SKULL_ITEM,1,(short)3);
		SkullMeta winM = (SkullMeta)winSkull.getItemMeta();
		
		if (winner.equals(p1)){
			winM.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "WINNER: " + ChatColor.AQUA + p1);
		}else{
			winM.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "WINNER: " + ChatColor.AQUA + p2);
		}
		
		winSkull.setItemMeta(winM);
		
		for (Inventory inv : frames){
			
			for (int slot = 0; slot <= 44; slot++){
				ItemStack i = inv.getItem(slot);
				if (i != null ){
					if (i.getType().equals(Material.WOOD_HOE)){
						inv.setItem(slot, p1skull);
					}
					if (i.getType().equals(Material.STONE_HOE)){
						inv.setItem(slot, p2skull);
					}
					if (i.getType().equals(Material.DIAMOND_HOE)){
						inv.setItem(slot, winSkull);
					}
				}
			}
		}
	}
	
	public Inventory getFrame(int frame){
		return frames.get(frame);
	}
	
	public void clear(){
		frames.clear();
		p1 = null;
		p2 = null;
		winner = null;
	}
	
}
