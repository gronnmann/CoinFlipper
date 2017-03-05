package io.github.gronnmann.utils;

public class GeneralUtils {
	public static int getIntInString(String string){
		int which = 0;
		int foundId = 0;
		
		for (String str : string.split(" ")){
			try{
				Integer.parseInt(str);
				
				foundId = which;
			}catch(Exception e){
			}
			which++;
		}
		return Integer.parseInt(string.split(" ")[foundId]);
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
