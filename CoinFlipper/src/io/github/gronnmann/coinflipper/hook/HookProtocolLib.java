package io.github.gronnmann.coinflipper.hook;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import me.gronnmann.utils.signinput.SignInputAPI;

public class HookProtocolLib {
	private HookProtocolLib(){}
	private static HookProtocolLib proto = new HookProtocolLib();
	public static HookProtocolLib getHook(){
		return proto;
	}
	
	private SignInputAPI api;
	private ProtocolManager mng;
	
	public void register(Plugin pl){
		mng = ProtocolLibrary.getProtocolManager();
		api = new SignInputAPI(pl, mng);
	}
	
	public void openSignInput(Player p){
		api.openEditor(p);
	}
}
