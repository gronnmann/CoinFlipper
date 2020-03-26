package io.github.gronnmann.utils.coinflipper;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;

import io.github.gronnmann.coinflipper.ConfigManager;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.CoinFlipper;

public class GeneralUtils {
	public static int getIntInString(String string){
		String found = string.replaceAll("[^\\d]", "");
		return Integer.parseInt(found);
	}
	
	public static String getFormattedNumbers(double number){
		
		if (number < 1000)return number+"";
		
		if (ConfigVar.FORMATTING_SHORTEN_MONEY.getBoolean()){
			
			
			int exponent = (int) (Math.log(number)/Math.log(1000));
			
			if (exponent > 4){
				exponent = 4;
			}
			
			char numSuffix = "kMBT".charAt(exponent-1);
		
			Debug.print("Formatting exponent: " + exponent);
			
			return String.format("%.01f %c", number/Math.pow(1000, exponent), numSuffix);
		}else{
			return CoinFlipper.getEcomony().format(number);
			/*DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
			return formatter.format(number);*/
		}
		
		
		
		
		
	}
	
	public static boolean isInt(String str){
		try{
			Integer.parseInt(str);
			
			return true;
		}catch(Exception e){
			return false;
		}
	}
	public static boolean isDouble(String str){
		try{
			Double.parseDouble(str);
			
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static boolean isBoolean(String str){
		if (str.equals("true")||str.equals("false"))return true;
		else return false;
	}
	
	public static String getJsonString(String field, String value){
		return "{\""+field+"\":\"" + value + "\"}";
	}
	
	
	public static int getMinecraftVersion() {
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		return Integer.parseInt(packageName.split("_")[1]);
	}
	
}
