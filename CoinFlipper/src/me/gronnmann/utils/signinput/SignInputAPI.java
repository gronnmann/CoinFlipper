package me.gronnmann.utils.signinput;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;

public class SignInputAPI {
	
	private ProtocolManager manager;
	
	public SignInputAPI(Plugin pl, ProtocolManager manager){
		this.manager = manager;
		
		
		manager.addPacketListener(new SignChangeDetector(pl, ListenerPriority.NORMAL));
		
	}
	
	public void openEditor(Player player){
		PacketContainer signUse = new PacketContainer(PacketType.Play.Server.OPEN_SIGN_EDITOR);
		signUse.getBlockPositionModifier().write(0, new BlockPosition(0, 928, 0));
		
		try {
			manager.sendServerPacket(player, signUse);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}


class SignChangeDetector extends PacketAdapter{
	public SignChangeDetector(Plugin pl, ListenerPriority priority){
		super(pl, priority, PacketType.Play.Client.UPDATE_SIGN);
	}
	
	public void onPacketReceiving(PacketEvent e){
		String[] signLines = e.getPacket().getStringArrays().read(0);
		Player who = e.getPlayer();
		
		if (e.getPacket().getBlockPositionModifier().read(0).equals(new BlockPosition(0, 928, 0))){
			Bukkit.getPluginManager().callEvent(new SignInputEvent(signLines, who));
		}
	}
	
}
