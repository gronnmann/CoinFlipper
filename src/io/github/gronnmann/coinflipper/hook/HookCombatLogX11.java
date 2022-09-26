package io.github.gronnmann.coinflipper.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;

public final class HookCombatLogX11 {
	private static final HookCombatLogX11 hook = new HookCombatLogX11();

	public static HookCombatLogX11 getHook(){
		return hook;
	}

	private ICombatManager combatManager;

	private HookCombatLogX11() {
		// Empty Constructor
	}

	public void register(){
		PluginManager pluginManager = Bukkit.getPluginManager();
		Plugin plugin = pluginManager.getPlugin("CombatLogX");
		ICombatLogX combatLogX = (ICombatLogX) plugin;
		this.combatManager = combatLogX.getCombatManager();
	}
	
	public boolean isTagged(Player player) {
	    return this.combatManager.isInCombat(player);
	}
}
