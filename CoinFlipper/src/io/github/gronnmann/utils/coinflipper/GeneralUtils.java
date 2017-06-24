package io.github.gronnmann.utils.coinflipper;

import java.text.DecimalFormat;

import io.github.gronnmann.coinflipper.ConfigManager;

public class GeneralUtils {
	public static int getIntInString(String string){
		String found = string.replaceAll("[^\\d]", "");
		return Integer.parseInt(found);
	}
	
	public static String getFormattedNumbers(double number){
		
		if (number < 1000)return number+"";
		
		if (ConfigManager.getManager().getConfig().getBoolean("formatting_shorten_money")){
			int exponent = (int) (Math.log(number)/Math.log(1000));
		
			char numSuffix = "kMBT".charAt(exponent-1);
		
			return String.format("%.01f %c", number/Math.pow(1000, exponent), numSuffix);
		}else{
			DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
			return formatter.format(number);
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
	
	public static String getJsonString(String field, String value){
		return "{\""+field+"\":\"" + value + "\"}";
	}
	
}
