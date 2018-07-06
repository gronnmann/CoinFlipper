package io.github.gronnmann.utils.coinflipper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {
	
	public static Material[] tool = {Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SPADE, Material.DIAMOND_HOE,
			Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SPADE, Material.IRON_HOE,
			Material.GOLD_PICKAXE, Material.GOLD_AXE, Material.GOLD_SPADE, Material.GOLD_HOE,
			Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SPADE, Material.STONE_HOE,
			Material.WOOD_PICKAXE, Material.WOOD_AXE, Material.WOOD_SPADE, Material.WOOD_HOE};
	public static  Material[] sword = {Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD};
	public static Material[] armor = {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
			Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS,
			Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
			Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
	public static Material[] boots = {Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLD_BOOTS, Material.IRON_BOOTS, Material.LEATHER_BOOTS};
	
	public static Material[] pickaxe = {Material.DIAMOND_PICKAXE, Material.GOLD_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE, Material.WOOD_PICKAXE};
	public static Material[] axe = {Material.DIAMOND_AXE, Material.GOLD_AXE, Material.IRON_AXE, Material.STONE_AXE, Material.WOOD_AXE};
	public static Material[] spade = {Material.DIAMOND_SPADE, Material.GOLD_SPADE, Material.IRON_SPADE, Material.STONE_SPADE, Material.WOOD_SPADE};
	public static Material[] hoe = {Material.DIAMOND_HOE, Material.GOLD_HOE, Material.IRON_HOE, Material.STONE_HOE, Material.WOOD_HOE};

	
	public static boolean isTool(ItemStack i){
		for (Material m : tool){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	public static boolean isSword(ItemStack i){
		for (Material m : sword){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	public static boolean isArmor(ItemStack i){
		for (Material m : armor){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	public static boolean isBoots(ItemStack i){
		for (Material m : boots){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	
	public static boolean isPickaxe(ItemStack i){
		for (Material m : pickaxe){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	public static boolean isAxe(ItemStack i){
		for (Material m : axe){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	public static boolean isSpade(ItemStack i){
		for (Material m : spade){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	public static boolean isHoe(ItemStack i){
		for (Material m : hoe){
			if (m.equals(i.getType()))return true;
		}
		return false;
	}
	
	public static int getDataAmount(Material material){
		switch(material){
			case WOOD: return 6;
			case STONE: return 7;
			case DIRT: return 3;
			case SAPLING: return 6;
			case SAND: return 2;
			case LOG: return 4;
			case LOG_2: return 2;
			case LEAVES: return 4;
			case LEAVES_2: return 2;
			case CARPET: return 16;
			case WOOL: return 15;
			case STAINED_GLASS: return 16;
			case STAINED_GLASS_PANE: return 16;
			case STAINED_CLAY: return 16;
			case SANDSTONE: return 3;
			case RED_SANDSTONE: return 3;
			case DOUBLE_PLANT: return 3;
			case SMOOTH_BRICK: return 4;
			case PRISMARINE: return 3;
			case COBBLE_WALL: return 2;
			case GOLDEN_APPLE: return 2;
			case INK_SACK: return 16;
			default: return 1;
			
		}
	}
	
	public static ItemStack createItem(Material material, String name, int data){
		ItemStack item = new ItemStack(material, 1, (short)data);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack createItem(Material material, String name){
		ItemStack item = new ItemStack(material, 1);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public static ItemStack getSkull(String name){
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
		itemMeta.setOwner(name);
		item.setItemMeta(itemMeta);
		return item;
	}
	
	public static ItemStack setName(ItemStack item, String name){
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(name);
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack setLore(ItemStack item, String lore){
		ItemMeta itemMeta = item.getItemMeta();
		
		List<String> lored = new ArrayList<String>();
		lored.add(lore);
		itemMeta.setLore(lored);
		
		item.setItemMeta(itemMeta);
		return item;
	}
	public static ItemStack addToLore(ItemStack item, String lore){
		ItemMeta itemMeta = item.getItemMeta();
		List<String> lored = new ArrayList<String>();
		if (itemMeta.getLore() != null){
			lored = itemMeta.getLore();
		}
		lored.add(lore);
		itemMeta.setLore(lored);
		
		item.setItemMeta(itemMeta);
		return item;
	}
}
