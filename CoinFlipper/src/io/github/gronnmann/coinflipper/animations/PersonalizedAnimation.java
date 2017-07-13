package io.github.gronnmann.coinflipper.animations;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
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
		
		p1Skull = ItemUtils.setName(p1Skull, MessagesManager.getMessage(Message.ANIMATION_ROLL_P1SKULL).replaceAll("%PLAYER%", p1));
		p2Skull = ItemUtils.setName(p2Skull, MessagesManager.getMessage(Message.ANIMATION_ROLL_P2SKULL).replaceAll("%PLAYER%", p2));
		winnerSkull = ItemUtils.setName(winnerSkull, MessagesManager.getMessage(Message.ANIMATION_ROLL_WINNERSKULL).replaceAll("%PLAYER%", winner));
		
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
							fram.setItem(slot, ItemUtils.setName(item, ChatColor.YELLOW + MessagesManager.getMessage(Message.ANIMATION_ROLL_ROLLING)));
						}else{
							fram.setItem(slot, ItemUtils.setName(item, ChatColor.GOLD + MessagesManager.getMessage(Message.ANIMATION_ROLL_ROLLING)));
						}
						
					}else{
						fram.setItem(slot, ItemUtils.setName(item, MessagesManager.getMessage(Message.ANIMATION_ROLL_WINNERCHOSEN)));
					}
				}
				
			}
		}
		
		return fram;
	}
}
