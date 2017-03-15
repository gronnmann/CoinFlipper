package io.github.gronnmann.utils;

public class GeneralUtils {
	public static int getIntInString(String string){
		String found = string.replaceAll("[^\\d]", "");
		return Integer.parseInt(found);
	}
	
	public static String getFormatted(double number){
		/*
		 * Code partly by aioobe on StackOverflow
		 * http://stackoverflow.com/questions/9769554/how-to-convert-number-into-k-thousands-m-million-and-b-billion-suffix-in-jsp
		 */
		
		if (number < 1000)return number+"";
		int exp = (int) (Math.log(number)/Math.log(1000));
		
		Debug.print(exp + "");
		
		return String.format("%.1f %c", number / Math.pow(1000, exp), "kMBT".charAt(exp-1));
	}
	
	public static boolean isInt(String str){
		try{
			Integer.parseInt(str);
			
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	
}
