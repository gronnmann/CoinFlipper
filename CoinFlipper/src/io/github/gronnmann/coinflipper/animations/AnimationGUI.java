package io.github.gronnmann.coinflipper.animations;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.gronnmann.coinflipper.CoinFlipper;
import io.github.gronnmann.coinflipper.GamesManager;
import io.github.gronnmann.coinflipper.customizable.Message;
import io.github.gronnmann.coinflipper.events.AnimationCloneEvent;
import io.github.gronnmann.coinflipper.events.AnimationCreateEvent;
import io.github.gronnmann.coinflipper.events.AnimationDeleteEvent;
import io.github.gronnmann.coinflipper.events.AnimationFrameChangeEvent;
import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.InventoryUtils;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.coinflipper.ReflectionUtils;
import io.github.gronnmann.utils.coinflipper.input.InputData;
import io.github.gronnmann.utils.coinflipper.input.InputData.InputType;
import io.github.gronnmann.utils.coinflipper.input.InputManager;
import io.github.gronnmann.utils.coinflipper.input.PlayerInputEvent;

public class AnimationGUI implements Listener{
	private AnimationGUI(){}
	private static AnimationGUI mng = new AnimationGUI();
	public static AnimationGUI getManager(){
		return mng;
	}
	
	private HashMap<String, Integer> accessMode = new HashMap<String, Integer>();
	
	public static int SLOT_NEW = 47, SLOT_DELETE = 51, SLOT_COPY = 49;
	
	public static int NEXT = 53, CURRENT = 52, PREV = 51, BACK = 45, P1I = 47, P2I = 48, WINNER = 49, CLONEPREV = 46;
	
	private static final String EXTRADATA_ANIM_NAME = "ANIMATION_NAME";
	
	private Inventory main;
	
	public void setup(){
		main = Bukkit.createInventory(new AnimationSelectorInventoryHolder(), 54, "CoinFlipper Animations");
		
		main.setItem(SLOT_NEW, ItemUtils.createItem(Material.WOOL, Message.ANIMATION_GUI_CREATE.getMessage(), 5));
		main.setItem(SLOT_DELETE, ItemUtils.createItem(Material.WOOL, Message.ANIMATION_GUI_DELETE.getMessage(), 14));
		main.setItem(SLOT_COPY, ItemUtils.createItem(Material.WOOL, Message.ANIMATION_GUI_CLONE.getMessage(), 11));
		InventoryUtils.fillWithItem(main, ItemUtils.createItem(Material.STAINED_GLASS_PANE, ".", 10), 36, 44);
		
		
	}
	
	private void reloadAnimations(){
		int slot = 0;
		for (int x = 0; x <= 35; x++){
			main.setItem(x, new ItemStack(Material.AIR));
		}
		for (Animation ani : AnimationsManager.getManager().getAnimations()){
			if (slot > 35)return;
			
			if (ani.isDefault()){
				ItemStack defaultAnim = ItemUtils.createItem(Material.CLAY, ani.getName());
				ItemUtils.setLore(defaultAnim, Message.ANIMATION_GUI_DEFANIM.getMessage());
				main.setItem(slot, defaultAnim);
				
				
			}else{
				main.setItem(slot, ItemUtils.createItem(Material.CLAY_BALL, ani.getName()));
			}
			slot++;
		}
	}
	
	
	
	public void openGUI(Player pl){
		this.reloadAnimations();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				InputManager.removeInputByPlayerAndID(pl.getName(), INPUT_COPY);
				InputManager.removeInputByPlayerAndID(pl.getName(), INPUT_NEW);
				
				
				pl.openInventory(main);
				
			}
		}.runTask(CoinFlipper.getMain());
	}
	
	
	private static final String INPUT_NEW = "ANIMATION_CREATE", INPUT_COPY = "ANIMATION_COPY";
	
	
	//Clicker
	@EventHandler
	public void mainMenuClicker(InventoryClickEvent e){
		if (!(e.getInventory().getHolder() instanceof AnimationSelectorInventoryHolder))return;
		
		if (e.isRightClick())return;
		
		if (e.getClickedInventory() == e.getWhoClicked().getInventory())return;
		e.setCancelled(true);
		if (e.getCurrentItem() == null)return;
		if (e.getCurrentItem().getItemMeta()==null)return;
		Player p = (Player) e.getWhoClicked();
		
		if (e.getSlot() == SLOT_NEW){
			p.sendMessage(Message.ANIMATION_CREATE_GIVENAME.getMessage());
			
			InputManager.requestInput(p.getName(), new InputData(INPUT_NEW, InputType.STRING));
			
		}else if (e.getSlot() == SLOT_DELETE){
			p.openInventory(this.getAnimationSelectorList());
			accessMode.put(p.getName(), 1);
		}else if (e.getSlot() == SLOT_COPY){
			p.openInventory(this.getAnimationSelectorList());
			accessMode.put(p.getName(), 2);
		}
		else{
			String name = e.getCurrentItem().getItemMeta().getDisplayName();
			Animation animation = AnimationsManager.getManager().getAnimation(name);
			if (animation == null)return;
			
			p.openInventory(this.getEditor(animation, 0));	
		}
		
	}
	
	@EventHandler
	public void defaultSetter(InventoryClickEvent e){
		if (!(e.getInventory().getHolder() instanceof AnimationSelectorInventoryHolder))return;
		
		if (!e.isRightClick())return;
		
		e.setCancelled(true);
		
		if (!(e.getCurrentItem().getType().equals(Material.CLAY_BALL)))return;
		
		String name = e.getCurrentItem().getItemMeta().getDisplayName();
		Animation animation = AnimationsManager.getManager().getAnimation(name);
		if (animation == null)return;
		
		AnimationsManager.getManager().setDefault(animation);
		
		this.reloadAnimations();
			
	}
	
	
	public Inventory getAnimationSelectorList(){
		Inventory selectorList = Bukkit.createInventory(new AnimationChooserInventoryHolder(), 54, "CoinFlipper: Choose animation");
		
		int slot = 0;
		for (Animation ani : AnimationsManager.getManager().getAnimations()){
			if (slot > 44)break;
			if (ani.isDefault()){
				ItemStack defaultAnim = ItemUtils.createItem(Material.CLAY, ani.getName());
				ItemUtils.setLore(defaultAnim, Message.ANIMATION_GUI_DEFANIM.getMessage());
				selectorList.setItem(slot, defaultAnim);
			}else{
				selectorList.setItem(slot, ItemUtils.createItem(Material.CLAY_BALL, ani.getName()));
			}
			
			
			
			slot++;
			
			if (slot == BACK){
				slot++;
			}
		}
		
		selectorList.setItem(BACK, ItemUtils.createItem(Material.INK_SACK, Message.ANIMATION_FRAMEEDITOR_BACK.getMessage(), 1));
		
		return selectorList;
	}
	
	
	@EventHandler
	public void copyOrDelete(InventoryClickEvent e){
		if (!(e.getInventory().getHolder() instanceof AnimationChooserInventoryHolder))return;
		ItemStack animI = e.getCurrentItem();
		
		if (animI == null)return;
		
		if (animI.getType().equals(Material.AIR))return;
		
		e.setCancelled(true);
		
		Player p = (Player) e.getWhoClicked();
		
		if (e.getSlot() == BACK){
			accessMode.remove(p.getName());
			this.openGUI(p);
			return;
			
		}
		
		String animName = animI.getItemMeta().getDisplayName();
		
		
		Animation anim = AnimationsManager.getManager().getAnimation(animName);
		if (anim == null)return;
		
		if (!accessMode.containsKey(p.getName()))return;
		
		int mode = accessMode.get(p.getName());
		
		
		switch(mode){
		case 1:
			
			AnimationDeleteEvent delEvent = new AnimationDeleteEvent(anim);
			if (delEvent.isCancelled())return;
			
			if (AnimationsManager.getManager().getDefault().equals(anim)){
				p.sendMessage(Message.ANIMATION_REMOVE_CANT_REMOVE_DEFAULT.getMessage());
				return;
			}
			
			if (AnimationsManager.getManager().getAnimations().size() <= 1){
				p.sendMessage(Message.ANIMATION_REMOVE_CANT_REMOVE_ALL.getMessage());
				return;
			}
			
			AnimationsManager.getManager().removeAnimation(anim);
			p.sendMessage(Message.ANIMATION_REMOVE_SUCCESS.getMessage().replace("%ANIMATION%", anim.getName()));
			accessMode.remove(p.getName());
			openGUI(p);
			break;
		case 2:
			
			p.sendMessage(Message.ANIMATION_CLONE_GIVENAME.getMessage());
			
			InputData data = new InputData(INPUT_COPY, InputType.STRING);
			data.addExtraData(EXTRADATA_ANIM_NAME, anim.getName());
			
			InputManager.requestInput(p.getName(), data);
		default: return;
		}
		
	}
	
	@EventHandler
	public void stopMemoryLeaks2(InventoryCloseEvent e){
		if (!(e.getInventory().getHolder() instanceof AnimationChooserInventoryHolder))return;
		accessMode.remove(e.getPlayer().getName());
	}
	
	
	public Inventory getEditor(Animation animation, int frame){
		Inventory editor = Bukkit.createInventory(new AnimationEditorInventoryHolder(), 54, "Animation editor: " + animation.getName() + " " + frame);
		
		editor.setContents(animation.getFrame(frame).getContents());
		
		ItemStack next = ItemUtils.createItem(Material.ARROW, Message.ANIMATION_FRAMEEDITOR_NEXT.getMessage());
		ItemStack prev = ItemUtils.createItem(Material.ARROW, Message.ANIMATION_FRAMEEDITOR_PREV.getMessage());
		ItemStack current = ItemUtils.createItem(Material.GLASS, Message.ANIMATION_FRAMEEDITOR_CURRENT.getMessage().replace("%FRAME%", frame+""));
		current.setAmount(frame);
		
		editor.setItem(PREV, prev);
		editor.setItem(CURRENT, current);
		editor.setItem(NEXT, next);
		
		editor.setItem(BACK, ItemUtils.createItem(Material.INK_SACK, Message.ANIMATION_FRAMEEDITOR_BACK.getMessage(), 1));
		
		editor.setItem(P1I, ItemUtils.createItem(Material.WOOD_HOE, Message.ANIMATION_FRAMEEDITOR_P1HEAD.getMessage()));
		editor.setItem(P2I, ItemUtils.createItem(Material.STONE_HOE, Message.ANIMATION_FRAMEEDITOR_P2HEAD.getMessage()));
		editor.setItem(WINNER, ItemUtils.createItem(Material.DIAMOND_HOE, Message.ANIMATION_FRAMEEDITOR_WINNERHEAD.getMessage()));
		
		editor.setItem(CLONEPREV, ItemUtils.createItem(Material.PAPER, Message.ANIMATION_FRAMEEDITOR_COPYLAST.getMessage()));
		
		return editor;
	}
	
	public void saveFrame(Animation animation , int frame, Inventory editor){
		for (int i = 45; i <= 53; i++){
			editor.setItem(i, new ItemStack(Material.AIR));
		}
		animation.getFrame(frame).clear();
		for (int i = 0;i <= 44; i++){
			animation.getFrame(frame).setItem(i, editor.getItem(i));
		}
	}
	
	@EventHandler
	public void editorManagement(InventoryClickEvent e){
		if (!(e.getInventory().getHolder() instanceof AnimationEditorInventoryHolder))return;
		for (int i = 45; i <= 53; i++){
			if (e.getSlot() == i){
				e.setCancelled(true);
			}
		}
		Player p = (Player) e.getWhoClicked();
		
		
		String invName = ReflectionUtils.getInventoryName(e.getInventory());
		
		int frameId = Integer.parseInt(invName.split(" ")[3]);
		
		String animationName = invName.split(" ")[2];
		Animation anim = AnimationsManager.getManager().getAnimation(animationName);
		
		if (e.getSlot() == NEXT){
			if (frameId == 50)return;
			
			AnimationFrameChangeEvent frameChange = new AnimationFrameChangeEvent(anim, frameId, frameId+1);
			Bukkit.getPluginManager().callEvent(frameChange);
			if (frameChange.isCancelled())return;
			
			
			this.saveFrame(anim, frameId, e.getInventory());
			p.openInventory(this.getEditor(anim, frameId+1));
			
		}
		if (e.getSlot() == PREV){
			if (frameId == 0 )return;
			
			AnimationFrameChangeEvent frameChange = new AnimationFrameChangeEvent(anim, frameId, frameId-1);
			Bukkit.getPluginManager().callEvent(frameChange);
			if (frameChange.isCancelled())return;
			
			this.saveFrame(anim, frameId, e.getInventory());
			p.openInventory(this.getEditor(anim, frameId-1));
		}
		if (e.getSlot() == BACK){
			this.openGUI(p);
		}
		if (e.getSlot() == P1I || e.getSlot() == P2I || e.getSlot() == WINNER){
			e.setCursor(e.getCurrentItem().clone());
		}
		if (e.getSlot() == CLONEPREV){
			if (frameId == 0)return;
			for (int i = 0;i <= 44; i++){
				e.getInventory().setItem(i, anim.getFrame(frameId-1).getItem(i));
			}
		}
	}
	
	//Save on close
	@EventHandler
	public void closeSaver(InventoryCloseEvent e){
		if (!(e.getInventory().getHolder() instanceof AnimationEditorInventoryHolder))return;
		
		
		String invName = ReflectionUtils.getInventoryName(e.getInventory());
		
		int frameId = Integer.parseInt(invName.split(" ")[3]);
		
		String animationName = invName.split(" ")[2];
		Animation anim = AnimationsManager.getManager().getAnimation(animationName);
		
		this.saveFrame(anim, frameId, e.getInventory());
	}
	
		
	@EventHandler
	public void handleInput(PlayerInputEvent e) {
		
		InputData params = e.getParams();
		
		Debug.print(params.getId());
		if (!(params.getId().equals(INPUT_NEW) || params.getId().equals(INPUT_COPY)))return;
		
		Player p = e.getPlayer();
		if (e.isExiting() ) {
			openGUI(p);
		}
		
		String animation = (String) e.getData();
		
		if (AnimationsManager.getManager().getAnimation(animation) != null){
			e.getPlayer().sendMessage(Message.ANIMATION_CREATE_ALREADYEXISTS.getMessage().replace("%ANIMATION%", animation));
			e.setCancelled(true);
			return;
		}
		
		
		if (params.getId().equals(INPUT_NEW)) {
			AnimationCreateEvent createEvent = new AnimationCreateEvent(animation);
			Bukkit.getPluginManager().callEvent(createEvent);
			
			if (!createEvent.isCancelled()){
				AnimationsManager.getManager().createAnimation(animation);
				e.getPlayer().sendMessage(Message.ANIMATION_CREATE_SUCCESS.getMessage().replace("%ANIMATION%", animation));
			}
			openGUI(e.getPlayer());
			
			
			
		}else if (params.getId().equals(INPUT_COPY)) {
			String copiedS = (String) params.getExtraData(EXTRADATA_ANIM_NAME);
			AnimationCloneEvent cloneEvent = new AnimationCloneEvent(animation, AnimationsManager.getManager().getAnimation(copiedS));
			Bukkit.getPluginManager().callEvent(cloneEvent);
			
			if (!cloneEvent.isCancelled()){
				Animation copied = AnimationsManager.getManager().createAnimation(animation);
				AnimationsManager.getManager().getAnimation(copiedS).copy(copied);
				e.getPlayer().sendMessage(Message.ANIMATION_CLONE_SUCCESS.getMessage());
				openGUI(e.getPlayer());
			}
			
		}
			
	}
	
	
	@EventHandler
	public void stopMemoryLeaks(PlayerQuitEvent e) {
		accessMode.remove(e.getPlayer().getName());
		
		GamesManager.getManager().setSpinning(e.getPlayer().getName(), false);
	}
	
}

class AnimationChooserInventoryHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}

class AnimationEditorInventoryHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}

class AnimationSelectorInventoryHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}
