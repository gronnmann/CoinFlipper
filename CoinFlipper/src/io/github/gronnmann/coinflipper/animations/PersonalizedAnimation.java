package io.github.gronnmann.coinflipper.animations;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.utils.coinflipper.InventoryUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import net.md_5.bungee.api.ChatColor;

public class PersonalizedAnimation {
	
	private String  inventoryName;
	private Animation animation;
	private ItemStack p1Skull, p2Skull, winnerSkull;
	
	public PersonalizedAnimation(Animation animation, String winner, String p1, String p2, String inventoryName){
		this.inventoryName = inventoryName;
		this.animation = animation;
		
		p1Skull = ItemUtils.getSkull(p1);
		p2Skull = ItemUtils.getSkull(p2);
		winnerSkull = ItemUtils.getSkull(winner);
		
		p1Skull = ItemUtils.setName(p1Skull, Message.ANIMATION_ROLL_P1SKULL.getMessage().replaceAll("%PLAYER%", p1));
		p2Skull = ItemUtils.setName(p2Skull, Message.ANIMATION_ROLL_P2SKULL.getMessage().replaceAll("%PLAYER%", p2));
		winnerSkull = ItemUtils.setName(winnerSkull, Message.ANIMATION_ROLL_WINNERSKULL.getMessage().replaceAll("%PLAYER%", winner));
		
	}
	
	public Inventory getFrame(int frame){
		Inventory fram = animation.getFrame(frame);
		fram = InventoryUtils.changeName(fram, inventoryName);
		
		for (int slot = 0; slot <= 44; slot++){
			ItemStack item = fram.getItem(slot);
			if (item != null){
				
				if (item.getType().equals(Material.WOOD_HOE)){
					fram.setItem(slot, p1Skull);
				}
				else if (item.getType().equals(Material.STONE_HOE)){
					fram.setItem(slot, p2Skull);
				}else if (item.getType().equals(Material.DIAMOND_HOE)){
					fram.setItem(slot, winnerSkull);
				}else{
					if (frame < 30){
						if (frame % 2 == 0){
							fram.setItem(slot, ItemUtils.setName(item, ChatColor.YELLOW + Message.ANIMATION_ROLL_ROLLING.getMessage()));
						}else{
							fram.setItem(slot, ItemUtils.setName(item, ChatColor.GOLD + Message.ANIMATION_ROLL_ROLLING.getMessage()));
						}
						
					}else{
						fram.setItem(slot, ItemUtils.setName(item, Message.ANIMATION_ROLL_WINNERCHOSEN.getMessage()));
					}
				}
				
			}
		}
		
		return fram;
	}
}
