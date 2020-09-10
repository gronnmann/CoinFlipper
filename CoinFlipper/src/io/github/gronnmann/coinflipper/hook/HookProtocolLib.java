package io.github.gronnmann.coinflipper.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.signinput.coinflipper.SignInputAPI;

public class HookProtocolLib {
	private HookProtocolLib(){}
	private static HookProtocolLib proto = new HookProtocolLib();
	public static HookProtocolLib getHook(){
		return proto;
	}
	
	private SignInputAPI api;
	private ProtocolManager mng;
	
	public boolean register(Plugin pl){
		
		if (GeneralUtils.getMinecraftVersion() < 18 || GeneralUtils.getMinecraftVersion() > 19) {
			System.out.println("[CoinFlipper] ProtocolLib is only supported in MC 1.18 or 1.19");
			return false;
		}
		
		
		try {
			
			mng = ProtocolLibrary.getProtocolManager();
			api = new SignInputAPI(pl, mng);
			return true;
		}catch(Exception e){
			Debug.print("Failed hooking into ProtocolLib");
			return false;
		}
	}
	
	public void openSignInput(Player p){
		api.openEditor(p);
	}
	
	
	public void disable() {
		api.disable();
	}
}
