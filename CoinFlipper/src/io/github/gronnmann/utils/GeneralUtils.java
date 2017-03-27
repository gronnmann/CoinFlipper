package io.github.gronnmann.utils;

public class GeneralUtils {
	public static int getIntInString(String string){
		String found = string.replaceAll("[^\\d]", "");
		return Integer.parseInt(found);
	}
	
	public static String getFormattedNumbers(double number){
		
		if (number < 1000)return number+"";
		
		int exponent = (int) (Math.log(number)/Math.log(1000));
		
		char numSuffix = "kMBTQ".charAt(exponent-1);
		
		return String.format("%.01f %c", number/Math.pow(1000, exponent), numSuffix);
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
