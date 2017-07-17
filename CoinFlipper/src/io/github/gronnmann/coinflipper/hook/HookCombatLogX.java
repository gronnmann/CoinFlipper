package io.github.gronnmann.coinflipper.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.Combat;

import net.minelink.ctplus.CombatTagPlus;

public class HookCombatLogX {
	private HookCombatLogX(){}
	private static HookCombatLogX hclx = new HookCombatLogX();
	public static HookCombatLogX getHook(){
		return hclx;
	}
	
	
	
	
	public boolean isTagged(Player pl){
		return Combat.in(pl);
	}
}
