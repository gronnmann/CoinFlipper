package io.github.gronnmann.coinflipper.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;

public final class HookCombatLogX11 {
	private HookCombatLogX11() {}
	private static final HookCombatLogX11 hook = new HookCombatLogX11();
	public static HookCombatLogX11 getHook(){
		return hook;
	}

	private ICombatManager combatManager;
	public void register(){
		ICombatLogX combatLogX = (ICombatLogX) Bukkit.getPluginManager().getPlugin("CombatLogX");
		this.combatManager = combatLogX.getCombatManager();
	}
	
	public boolean isTagged(Player player) {
	    return this.combatManager.isInCombat(player);
	}
}
