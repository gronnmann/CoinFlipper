package io.github.gronnmann.utils.signinput.coinflipper;

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
import com.comphenix.protocol.wrappers.WrappedChatComponent;

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
	
	String packageName = Bukkit.getServer().getClass().getPackage().getName();
	int vID = Integer.parseInt(packageName.split("_")[1]);
	
	public SignChangeDetector(Plugin pl, ListenerPriority priority){
		super(pl, priority, PacketType.Play.Client.UPDATE_SIGN);
	}
	
	public void onPacketReceiving(PacketEvent e){
		String[] signLine;
		Player who = e.getPlayer();
		
		if (e.getPacket().getBlockPositionModifier().read(0).equals(new BlockPosition(0, 928, 0))){
			
			if (vID < 9){
				WrappedChatComponent[] text = e.getPacket().getChatComponentArrays().read(0);
				
				String[] lines = {text[0].toString().split("\"")[1],
						text[1].toString().split("\"")[1],
						text[2].toString().split("\"")[1],
						text[3].toString().split("\"")[1]};
				
				signLine = lines;
				
			}else{
				signLine = e.getPacket().getStringArrays().read(0);
			}
			
			Bukkit.getPluginManager().callEvent(new SignInputEvent(signLine, who));
		}
		
	}
	
}