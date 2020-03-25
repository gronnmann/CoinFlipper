package io.github.gronnmann.utils.coinflipper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class ReflectionUtils {
	
	
	
	public static String getInventoryName(Inventory inv) {
		
		String title;
		
		try {
			if (GeneralUtils.getMinecraftVersion() >= 14) {
				
				String packageName = Bukkit.getServer().getClass().getPackage().getName();
				
				
				Class<?> invClass = Class.forName(packageName + ".inventory.CraftInventory");
				
				Field iinventory = invClass.getDeclaredField("inventory");
				iinventory.setAccessible(true);
				
				
				Object iinv = iinventory.get(inv);
				
				
				
				Method getTitle = iinv.getClass().getMethod("getTitle");
				getTitle.setAccessible(true);
				
				title = (String) getTitle.invoke(iinv);
				
				
				getTitle.setAccessible(false);
				
				Debug.print("Reflection copying inventory (1.14+): " +  title);
				
				iinventory.setAccessible(false);
				
				
				
				//return (String)name;
				
				//return "";
				
			}else {
				Method getName18 = inv.getClass().getMethod("getName");

				title = (String) getName18.invoke(inv);
				
				Debug.print("Reflection copying inventory (-1.14): " +  title);
			}
		}
		catch(Exception e) {
			Debug.print("Failed fetching inventory name for " + inv);
			Debug.print(e.getStackTrace().toString());
			
			e.printStackTrace();
			
			title = "";
		}
		
		
		return title;
	}
}
