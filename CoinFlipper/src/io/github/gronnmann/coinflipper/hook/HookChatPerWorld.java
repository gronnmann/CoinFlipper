package io.github.gronnmann.coinflipper.hook;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import io.github.gronnmann.chatperworld.ChatManager;

public class HookChatPerWorld {
	private HookChatPerWorld(){}
	private static HookChatPerWorld hcpw = new HookChatPerWorld();
	public static HookChatPerWorld getHook(){
		return hcpw;
	}
	
	
	
	public ArrayList<Player> getReceivers(Player p){
		return ChatManager.getReceivers(p);
	}
}
