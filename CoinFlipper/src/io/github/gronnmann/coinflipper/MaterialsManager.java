package io.github.gronnmann.coinflipper;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MaterialsManager {
	private MaterialsManager(){}
	
	private static FileConfiguration file;
	private static Plugin pl;
	
	public static void setup(Plugin p){
		file = ConfigManager.getManager().getMaterials();
		pl = p;
	}
	
	public static Material getMaterial(String name){
		if (!file.contains(name)){
			System.out.println("[CoinFlipper] Could not get material " + name + ". Attempting to copy defaults.");
			return MaterialsManager.updateMaterial(name);
		}
		return Material.valueOf(file.getString(name));
	}
	public static int getData(String name){
		if (!file.contains(name+"_data")){
			MaterialsManager.updateMaterial(name);
			return 0;
		}
		return file.getInt(name+"_data");
	}
	
	private static Material updateMaterial(String name){
		try{
			InputStream newConfigStream = pl.getClass().getResourceAsStream("/materials.yml");
			
			if (newConfigStream == null)return Material.AIR;
			
			InputStreamReader newConfigStreamReader = new InputStreamReader(newConfigStream);
			
			FileConfiguration newConfig = YamlConfiguration.loadConfiguration(newConfigStreamReader);
			
			if (newConfig.contains(name)){
				file.set(name, newConfig.get(name));
				System.out.println("[CoinFlipper] Fixed missing material " + name + " (" + newConfig.getString(name) + ")");
			}else{
				file.set(name, Material.AIR);
				System.out.println("[CoinFlipper] Could not generate default material with ID " +
				name + ". Please get orginal at https://www.spigotmc.org/resources/coinflipper.33916");
			}
			
			if (newConfig.contains(name+"_data")){
				file.set(name+"_data", newConfig.get(name+"_data"));
				System.out.println("[CoinFlipper] Fixed missing material data" + name + "_data (" + newConfig.getInt(name+"_data") + ")");
			}else{
				file.set(name+"_data", 0);
				System.out.println("[CoinFlipper] Could not generate default material data with ID " +
				name + "_data. Please get orginal at https://www.spigotmc.org/resources/coinflipper.33916");
			}
			
			
			ConfigManager.getManager().saveMaterials();
			
			newConfigStreamReader.close();
			newConfigStream.close();
			
			return Material.valueOf(file.getString(name));
			
		}catch(Exception e){
			e.printStackTrace();
			return Material.AIR;
		}
	}
	
}
