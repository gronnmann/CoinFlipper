package io.github.gronnmann.coinflipper.hook;

import com.SirBlobman.combatlogx.Combat;

import org.bukkit.entity.Player;

public class HookCombatLogX {
	private HookCombatLogX(){}
	private static HookCombatLogX hclx = new HookCombatLogX();
	public static HookCombatLogX getHook(){
		return hclx;
	}
	
	
	
	
	public boolean isTagged(Player pl){
	    return Combat.isInCombat(pl);
	}
}
