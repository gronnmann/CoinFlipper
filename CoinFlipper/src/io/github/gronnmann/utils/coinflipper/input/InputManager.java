package io.github.gronnmann.utils.coinflipper.input;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.customizable.ConfigVar;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.hook.HookManager;
import io.github.gronnmann.coinflipper.hook.HookManager.HookType;
import io.github.gronnmann.coinflipper.hook.HookProtocolLib;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.GeneralUtils;
import io.github.gronnmann.utils.coinflipper.input.InputData.InputType;
import io.github.gronnmann.utils.signinput.coinflipper.SignInputEvent;

public class InputManager implements Listener{
	protected static HashMap<String, InputData> inputData = new HashMap<>();
	private static final int TIMEOUT = 60*20;
	
	
	public static void setupListeners() {
		Bukkit.getPluginManager().registerEvents(new InputManager(), CoinFlipper.getMain());
		
		BooleanChooser.getInstance().setup();
		Bukkit.getPluginManager().registerEvents(BooleanChooser.getInstance(), CoinFlipper.getMain());
		
		SoundChooser.getInstance().setup();
		Bukkit.getPluginManager().registerEvents(SoundChooser.getInstance(), CoinFlipper.getMain());
	}
	
	public static void requestInput(String player, InputData data) {
		requestInput(player, data, true);
	}
	public static void requestInput(String player, InputData data, boolean closeInventory) {
		inputData.put(player, data);
		
		Player p = Bukkit.getPlayer(player);
		if (p == null)return;
		
		if (closeInventory) {
			p.closeInventory();
		}
		
		if (data.getType() == InputType.BOOLEAN) {
			BooleanChooser.getInstance().openEditor(p, data);
		}else if (data.getType() == InputType.SOUND) {
			SoundChooser.getInstance().refresh(((ConfigVar)data.getExtraData("CVAR")).getValue().toString());
			SoundChooser.getInstance().openEditor(p, (Inventory) data.getExtraData("RETURN_INVENTORY"), "CoinFlipper " + data.getExtraData("CVAR"));
		}
		
		Debug.print("Using PL: " + HookManager.getManager().isHooked(HookType.ProtocolLib));
		Debug.print("Sign input: " + ConfigVar.SIGN_INPUT.getBoolean());
		
		if (!(data.getType() == InputType.BOOLEAN || data.getType() == InputType.SOUND)) {
			if (HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean()
					&& CoinFlipper.versionId <= 12){
					Debug.print("sign");
					HookProtocolLib.getHook().openSignInput(p);
			}
		}
			
			
		new InputTimeout(p.getName(), data.getCreated()).runTaskLaterAsynchronously(CoinFlipper.getMain(), TIMEOUT);
	}

	public static boolean removeInputByPlayerAndID(String player, String id) {
		return inputData.remove(player, id);
	}
	
	public static boolean removeInput(String player) {
		return (inputData.remove(player) == null);
	}
	
	public static boolean isEditing(String player, String id) {
		if (inputData.get(player) != null && inputData.get(player).getId().equals(id)) {
			return true;
		}
		return false;
	}
	
	public static InputData getData(String player) {
		return inputData.get(player);
	}
	
	
	
	@EventHandler
	public void signInput(SignInputEvent e) {
		
		if (!HookManager.getManager().isHooked(HookType.ProtocolLib) && ConfigVar.SIGN_INPUT.getBoolean())return;
		Player p = e.getPlayer();
		
		if (!inputData.containsKey(p.getName()))return;
		
		String input = e.getLine(0);
		
		if (!processInput(p.getName(), input)) {
			HookProtocolLib.getHook().openSignInput(e.getPlayer());
		}
		
	}
	
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void chatInput(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (!inputData.containsKey(p.getName()))return;
		InputData data = inputData.get(p.getName());
		if (data.getType() == InputType.BOOLEAN || data.getType() == InputType.SOUND)return;
		
		
		e.setCancelled(true);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				processInput(p.getName(), e.getMessage());
			}
		}.runTask(CoinFlipper.getMain());
		
	}
	
	protected static boolean processInput(String player, String input) {
		
		
		InputData data = inputData.get(player);
		if (data == null)return false;
		
		Player processed = Bukkit.getPlayer(player);
		if (processed == null) return false;
		
		Debug.print("Processing: " + player + ", input: " + input + ", type: " + data.getType().toString());
		
		input = ChatColor.stripColor(input);
		Object dataHolder = null;
		
		
		if (input.equals(data.getExitString())) {
			
			PlayerInputEvent event = new PlayerInputEvent(processed, data, dataHolder, true);
			
			Bukkit.getPluginManager().callEvent(event);
			
			return false;
		}
		
		
		if (data.getType() == InputType.STRING || data.getType() == InputType.TEXT) {
			if (input.length() < 1) {
				processed.sendMessage(Message.INPUT_EMPTY.getMessage());
				return false;
			}
			
			if (data.getType() == InputType.TEXT) {
				input = input.replace(" ", "_");
			}
			
			dataHolder = input;
			
		}else if (data.getType() == InputType.INTEGER || data.getType() == InputType.DOUBLE) {
			
			if (data.getType() == InputType.INTEGER) {
				if (!GeneralUtils.isInt(input)) {					
					processed.sendMessage(Message.INPUT_NOTNUM.getMessage());
					return false;
				}else {
					dataHolder = Integer.parseInt(input);
				}
			}else if (data.getType() == InputType.DOUBLE) {
				if (!GeneralUtils.isDouble(input)) {					
					processed.sendMessage(Message.INPUT_NOTNUM.getMessage());
					return false;
				}
			}
			
			double asDouble = Double.parseDouble(dataHolder.toString());
			
			if (data.hasMax() && data.getMax() < asDouble) {
				processed.sendMessage(Message.ERROR_OVER_MAX.getMessage());
				return false;
			}
			if (data.hasMin() && asDouble < data.getMin()) {
				processed.sendMessage(Message.ERROR_UNDER_MIN.getMessage());
				return false;
			}
				
				
		}else if (data.getType() == InputType.BOOLEAN) {
			if (!GeneralUtils.isBoolean(input)) {
				processed.sendMessage(Message.INPUT_NOTBOOLEAN.getMessage());
				return false;
			}
			dataHolder = Boolean.parseBoolean(input);
		}
		else if (data.getType() == InputType.SOUND) {
			try {
				Sound snd = Sound.valueOf(input);
				dataHolder = snd;
			}catch(Exception e) {
				processed.sendMessage(Message.INPUT_NOTSOUND.getMessage());
				return false;
			}
		}
			
			
			
		PlayerInputEvent event = new PlayerInputEvent(processed, data, dataHolder);
		
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled())return false;
		
		
		inputData.remove(player);
		return true;
		
		
	}
	
	@EventHandler
	public void stopMemoryLeak(PlayerQuitEvent e) {
		inputData.remove(e.getPlayer().getName());
	}
	
	
}

class InputTimeout extends BukkitRunnable{
	
	
	private String key;
	private long createdId;
	
	public InputTimeout(String key, long createdId) {
		this.key = key;
		this.createdId = createdId;
	}
	
	
	public void run() {
		
		if (!(InputManager.inputData.containsKey(key) && InputManager.inputData.get(key).getCreated() == createdId))return;
		
		InputManager.inputData.remove(key);
		Player t = Bukkit.getPlayer(key);
		if (t != null) {
			t.sendMessage(ChatColor.RED + "Input timed out.");
		}
	}
}