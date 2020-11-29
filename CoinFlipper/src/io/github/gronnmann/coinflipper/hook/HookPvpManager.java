package io.github.gronnmann.coinflipper.hook;

import org.bukkit.entity.Player;


public class HookPvpManager {
	private HookPvpManager(){}
	private static HookPvpManager hctp = new HookPvpManager();
	public static HookPvpManager getHook(){
		return hctp;
	}
	
	
	public void register(){
	}
	
	/* Hook removed till plugin gets maven.
	public boolean isTagged(Player pl){
		
		PvPlayer p = PvPlayer.get(pl);
		
		if (p == null){
			return false;
		}
		
		return p.isInCombat();

	}*/
}
