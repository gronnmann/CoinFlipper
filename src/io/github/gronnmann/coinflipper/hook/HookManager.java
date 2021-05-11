package io.github.gronnmann.coinflipper.hook;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;

public class HookManager {
	private HookManager(){}
	private static HookManager mng = new HookManager();
	public static HookManager getManager(){
		return mng;
	}
	
	private HashMap<HookType, Boolean> hooks = new HashMap<>();
	
	public enum HookType {CombatTagPlus, PvPManager, ProtocolLib, ChatPerWorld, CombatLogX};
	
	private Plugin pl;
	
	public void registerHooks(){
		
		this.pl = CoinFlipper.getMain();
		
		for (HookType type : HookType.values()){
			hooks.put(type, false);
		}
		
		if (ConfigVar.SIGN_INPUT.getBoolean() && (CoinFlipper.versionId == 8 || CoinFlipper.versionId == 9)) {
			Plugin ProtocolLib = Bukkit.getPluginManager().getPlugin("ProtocolLib");
			if (!(ProtocolLib == null)){
				this.setHooked(HookType.ProtocolLib);
			}
		}
		
		Plugin combatTagPlus = Bukkit.getPluginManager().getPlugin("CombatTagPlus");
		if (!(combatTagPlus == null)){
			this.setHooked(HookType.CombatTagPlus);
		}
		
		/*Plugin PvPManager = Bukkit.getPluginManager().getPlugin("PvPManager");
		if (!(PvPManager == null)){
			this.setHooked(HookType.PvPManager);
		}*/
		
		/*Plugin ChatPerWorld = Bukkit.getPluginManager().getPlugin("ChatPerWorld");
		if (!(ChatPerWorld == null)){
			this.setHooked(HookType.ChatPerWorld);
		}*/
		Plugin CombatLogX = Bukkit.getPluginManager().getPlugin("CombatLogX");
		if (!(CombatLogX == null)){
			this.setHooked(HookType.CombatLogX);
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
		case ProtocolLib:
			hooks.put(hook, HookProtocolLib.getHook().register(pl));
			break;
		case CombatLogX:
			String version = Bukkit.getPluginManager().getPlugin("CombatLogX").getDescription().getVersion();
			if(version.startsWith("10")) HookCombatLogX10.getHook().register();
			else if(version.startsWith("11")) HookCombatLogX11.getHook().register();
			break;
		}
		
	}
	
	public void onDisable() {
		if (isHooked(HookType.ProtocolLib)) {
			HookProtocolLib.getHook().disable(pl);
		}
	}
	
	public boolean isHooked(HookType hook){
		if (!hooks.containsKey(hook)){
			hooks.put(hook, false);
		}
		return hooks.get(hook);
	}
	
	public boolean isTagged(Player player) {
		if (this.isHooked(HookType.CombatTagPlus)){
			return HookCombatTagPlus.getHook().isTagged(player);
		}
		
		/*if (this.isHooked(HookType.PvPManager)){
			return HookPvpManager.getHook().isTagged(pl);
		}*/
		if (this.isHooked(HookType.CombatLogX)){
			String version = Bukkit.getPluginManager().getPlugin("CombatLogX").getDescription().getVersion();
			if(version.startsWith("10")) return HookCombatLogX10.getHook().isTagged(player);
			else if(version.startsWith("11")) HookCombatLogX11.getHook().isTagged(player);
		}
		
		return false;
	}
	
}
