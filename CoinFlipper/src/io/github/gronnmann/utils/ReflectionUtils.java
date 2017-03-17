package io.github.gronnmann.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflectionUtils {
	
	public enum TitleType{TITLE, SUBTITLE}
	
	public static void sendPacket(Player player, Object packet){
		try {
			Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
			Object connection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
			Method sendPacket = connection.getClass().getMethod("sendPacket", getServerClass("Packet"));
			Debug.print( packet.getClass().getName());
			
			sendPacket.invoke(player, packet);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Class<?> getServerClass(String afterPackage){
		String servPack = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		
		try {
			return Class.forName("net.minecraft.server." + servPack + "." + afterPackage);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	//Titles
	public static void sendTitle(Player player, String title, TitleType type){
		//PacketPlayOutTitle titleP = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"Test\"}"));
		//((CraftPlayer) player).getHandle().playerConnection.sendPacket(titleP);
		
		try{
			Constructor<?> titleConst = getServerClass("PacketPlayOutTitle")
					.getConstructor(getServerClass("PacketPlayOutTitle$EnumTitleAction"), 
					getServerClass("IChatBaseComponent"), int.class, int.class, int.class);
			
			Object titleType = getServerClass("PacketPlayOutTitle$EnumTitleAction").getField(type.toString()).get(null);
			Object message = getServerClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class)
					.invoke(null, GeneralUtils.getJsonString("text", title));
			
			Object titlePacket = titleConst.newInstance(titleType, message, 20, 20, 40);
			
			sendPacket(player, titlePacket);
			
		}catch(Exception e){
			e.printStackTrace();
		}
				
	}
	
	
}
