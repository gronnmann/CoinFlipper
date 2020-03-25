package io.github.gronnmann.utils.coinflipper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemUtils {
	
	public static Material[] tool = {Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,
			Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_HOE,
			Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE,
			Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_HOE,
			Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE};
	public static  Material[] sword = {Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD};
	public static Material[] armor = {Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
			Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
			Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
			Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
			Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS};
	public static Material[] boots = {Material.DIAMOND_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.LEATHER_BOOTS};
	
	public static Material[] pickaxe = {Material.DIAMOND_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, Material.STONE_PICKAXE, Material.WOODEN_PICKAXE};
	public static Material[] axe = {Material.DIAMOND_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.STONE_AXE, Material.WOODEN_AXE};
	public static Material[] spade = {Material.DIAMOND_SHOVEL, Material.GOLDEN_SHOVEL, Material.IRON_SHOVEL, Material.STONE_SHOVEL, Material.WOODEN_SHOVEL};
	public static Material[] hoe = {Material.DIAMOND_HOE, Material.GOLDEN_HOE, Material.IRON_HOE, Material.STONE_HOE, Material.WOODEN_HOE};

	
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
			case LEGACY_WOOD: return 6;
			case STONE: return 7;
			case DIRT: return 3;
			case LEGACY_SAPLING: return 6;
			case SAND: return 2;
			case LEGACY_LOG: return 4;
			case LEGACY_LOG_2: return 2;
			case LEGACY_LEAVES: return 4;
			case LEGACY_LEAVES_2: return 2;
			case LEGACY_CARPET: return 16;
			case LEGACY_WOOL: return 15;
			case LEGACY_STAINED_GLASS: return 16;
			case LEGACY_STAINED_GLASS_PANE: return 16;
			case LEGACY_STAINED_CLAY: return 16;
			case SANDSTONE: return 3;
			case RED_SANDSTONE: return 3;
			case LEGACY_DOUBLE_PLANT: return 3;
			case LEGACY_SMOOTH_BRICK: return 4;
			case PRISMARINE: return 3;
			case COBBLESTONE_WALL: return 2;
			case GOLDEN_APPLE: return 2;
			case INK_SAC: return 16;
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
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1, (short)3);
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
