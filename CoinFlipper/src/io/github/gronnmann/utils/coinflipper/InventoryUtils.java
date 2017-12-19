package io.github.gronnmann.utils.coinflipper;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
	public static void fillWithItem(Inventory inv, ItemStack item, int start, int end){
		for (;start <= end; start++){
			inv.setItem(start, item);
		}
	}
	
	public static Inventory changeName(Inventory inv, String name){
		Inventory newInv = Bukkit.createInventory(inv.getHolder(), inv.getSize(), name);
		newInv.setContents(inv.getContents());
		return newInv;
	}
	
	public static Inventory clone(Inventory inv){
		Inventory newInv = Bukkit.createInventory(inv.getHolder(), inv.getSize(), inv.getName());
		newInv.setContents(inv.getContents());
		return newInv;
	}
}
