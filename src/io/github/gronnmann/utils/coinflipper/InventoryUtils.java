package io.github.gronnmann.utils.coinflipper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
		Inventory newInv = Bukkit.createInventory(inv.getHolder(), inv.getSize(), ReflectionUtils.getInventoryName(inv));
		newInv.setContents(inv.getContents());
		return newInv;
	}
	
	public static int getAvailableSlots(ItemStack item, Inventory inv) {
		int count = 0;
		for (ItemStack i : inv.getContents()) {
			if (i == null || i.getType() == Material.AIR) {
				count+=item.getMaxStackSize();
			}else if (i.getType() == item.getType()) {
				count+= (i.getMaxStackSize() - i.getAmount());
			}
		}
		
		return count;
	}
}
