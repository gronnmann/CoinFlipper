package io.github.gronnmann.coinflipper.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.NoChance.PvPManager.PvPManager;
import me.NoChance.PvPManager.PvPlayer;
import me.NoChance.PvPManager.Managers.PlayerHandler;

public class HookPvpManager {
	private HookPvpManager(){}
	private static HookPvpManager hctp = new HookPvpManager();
	public static HookPvpManager getHook(){
		return hctp;
	}
	
	PvPManager man;
	PlayerHandler hndl;
	
	public void register(){
		man = (PvPManager) Bukkit.getPluginManager().getPlugin("PvPManager");
		
		hndl = man.getPlayerHandler();
		
	}
	
	public boolean isTagged(Player pl){
		
		PvPlayer p = hndl.get(pl);
		
		if (p == null){
			return false;
		}
		
		return p.isInCombat();

	}
}
