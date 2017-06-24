package io.github.gronnmann.coinflipper.hook;

import org.bukkit.entity.Player;

import me.NoChance.PvPManager.PvPlayer;

public class HookPvpManager {
	private HookPvpManager(){}
	private static HookPvpManager hctp = new HookPvpManager();
	public static HookPvpManager getHook(){
		return hctp;
	}
	
	
	public void register(){
	}
	
	public boolean isTagged(Player pl){
		
		PvPlayer p = PvPlayer.get(pl);
		
		if (p == null){
			return false;
		}
		
		return p.isInCombat();

	}
}
