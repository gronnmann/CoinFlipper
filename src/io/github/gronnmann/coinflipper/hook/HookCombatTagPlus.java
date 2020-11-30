package io.github.gronnmann.coinflipper.hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.minelink.ctplus.CombatTagPlus;

public class HookCombatTagPlus {
	private HookCombatTagPlus(){}
	private static HookCombatTagPlus hctp = new HookCombatTagPlus();
	public static HookCombatTagPlus getHook(){
		return hctp;
	}
	
	private CombatTagPlus ct;
	
	public void register(){
		ct = (CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus");
	}
	
	public boolean isTagged(Player pl){
		return ct.getTagManager().isTagged(pl.getUniqueId());
	}
}
