package io.github.gronnmann.coinflipper.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.utility.ICombatManager;


public class HookCombatLogX {
	private HookCombatLogX(){}
	private static HookCombatLogX hclx = new HookCombatLogX();
	public static HookCombatLogX getHook(){
		return hclx;
	}
	
	private ICombatLogX cl;
	private ICombatManager mng;
	
	public void register(){
		cl = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
		mng = cl.getCombatManager();
	}
	
	
	public boolean isTagged(Player pl){
	    return mng.isInCombat(pl);
	}
}
