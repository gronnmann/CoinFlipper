package io.github.gronnmann.coinflipper.hook;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class HookManager {
	private HookManager(){}
	private static HookManager mng = new HookManager();
	public static HookManager getManager(){
		return mng;
	}
	
	private HashMap<HookType, Boolean> hooks = new HashMap<>();
	
	public enum HookType {CombatTagPlus, PvPManager};
	
	public void registerHooks(){
		for (HookType type : HookType.values()){
			hooks.put(type, false);
		}
		
		Plugin combatTagPlus = Bukkit.getPluginManager().getPlugin("CombatTagPlus");
		if (!(combatTagPlus == null)){
			this.setHooked(HookType.CombatTagPlus);
		}
		
		Plugin PvPManager = Bukkit.getPluginManager().getPlugin("PvPManager");
		if (!(PvPManager == null)){
			this.setHooked(HookType.PvPManager);
		}
	}
	
	private void setHooked(HookType hook){
		System.out.println("[CoinFlipper] " + hook.toString() + " detected. Hooking...");
		
		hooks.remove(hook);
		hooks.put(hook, true);
		
		switch(hook){
		default: break;
		case CombatTagPlus:
			HookCombatTagPlus.getHook().register();
			break;
		case PvPManager:
			HookPvpManager.getHook().register();
			break;
		}
		
	}
	
	public boolean isHooked(HookType hook){
		if (!hooks.containsKey(hook)){
			hooks.put(hook, false);
		}
		return hooks.get(hook);
	}
	
	public boolean isTagged(Player pl){
		if (this.isHooked(HookType.CombatTagPlus)){
			return HookCombatTagPlus.getHook().isTagged(pl);
		}
		
		if (this.isHooked(HookType.PvPManager)){
			return HookPvpManager.getHook().isTagged(pl);
		}
		
		return false;
	}
	
}
