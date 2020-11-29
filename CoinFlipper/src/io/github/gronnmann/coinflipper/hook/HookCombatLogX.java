package io.github.gronnmann.coinflipper.hook;

import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.api.utility.ICombatManager;

public class HookCombatLogX {
	private HookCombatLogX(){}
	private static HookCombatLogX hclx = new HookCombatLogX();
	public static HookCombatLogX getHook(){
		return hclx;
	}
	
	
	
	
	public boolean isTagged(Player pl){
	    return CombatUtil.isInCombat(pl);
	}
}
