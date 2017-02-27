package io.github.gronnmann.coinflipper.animations;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.utils.InventoryUtils;
import io.github.gronnmann.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;

public class AnimationGUI implements Listener{
	private AnimationGUI(){}
	private static AnimationGUI mng = new AnimationGUI();
	public static AnimationGUI getManager(){
		return mng;
	}
	
	private HashMap<String, Integer> nameRetrieving = new HashMap<String, Integer>();
	
	public static int SLOT_NEW = 47, SLOT_DELETE = 51;
	
	public static int NEXT = 53, CURRENT = 52, PREV = 51, BACK = 45, P1I = 47, P2I = 48, WINNER = 49, CLONEPREV = 46;
	
	
	private Inventory main;
	
	public void setup(){
		main = Bukkit.createInventory(null, 54, "CoinFlipper Animations");
		
		main.setItem(SLOT_NEW, ItemUtils.createItem(Material.WOOL,  ChatColor.GREEN + ChatColor.BOLD.toString()+ "Create", 5));
		main.setItem(SLOT_DELETE, ItemUtils.createItem(Material.WOOL, ChatColor.GREEN + ChatColor.BOLD.toString() + "Delete", 14));
		InventoryUtils.fillWithItem(main, ItemUtils.createItem(Material.STAINED_GLASS_PANE, ".", 10), 36, 44);
		
		
	}
	
	private void reloadAnimations(){
		int slot = 0;
		for (int x = 0; x <= 35; x++){
			main.setItem(x, new ItemStack(Material.AIR));
		}
		for (Animation ani : AnimationsManager.getManager().getAnimations()){
			if (slot > 35)return;
			main.setItem(slot, ItemUtils.createItem(Material.CLAY_BALL, ani.getName()));
			slot++;
		}
	}
	
	
	
	public void openGUI(Player pl){
		this.reloadAnimations();
		pl.openInventory(main);
	}
	
	
	//Clicker
	@EventHandler
	public void mainMenuClicker(InventoryClickEvent e){
		if (!e.getInventory().getName().contains("CoinFlipper Animations"))return;
		if (e.getClickedInventory() == e.getWhoClicked().getInventory())return;
		e.setCancelled(true);
		if (e.getCurrentItem() == null)return;
		if (e.getCurrentItem().getItemMeta()==null)return;
		Player p = (Player) e.getWhoClicked();
		
		if (e.getSlot() == SLOT_NEW){
			p.closeInventory();
			p.sendMessage(ChatColor.YELLOW + "Please write the name you want the animation to create.");
			nameRetrieving.put(p.getName(), 0);
		}else if (e.getSlot() == SLOT_DELETE){
			p.closeInventory();
			p.sendMessage(ChatColor.YELLOW + "Please write the name of the animation you want to delete.");
			nameRetrieving.put(p.getName(), 1);
		}else{
			String name = e.getCurrentItem().getItemMeta().getDisplayName();
			Animation animation = AnimationsManager.getManager().getAnimation(name);
			if (animation == null)return;
			
			p.openInventory(this.getEditor(animation, 0));
			
		}
		
	}
	
	public Inventory getEditor(Animation animation, int frame){
		Inventory editor = Bukkit.createInventory(null, 54, "Animation editor: " + animation.getName() + " " + frame);
		
		editor.setContents(animation.getFrame(frame).getContents());
		
		ItemStack next = ItemUtils.createItem(Material.ARROW, ChatColor.GOLD + "Next");
		ItemStack prev = ItemUtils.createItem(Material.ARROW, ChatColor.GOLD + "Previous");
		ItemStack current = ItemUtils.createItem(Material.GLASS, ChatColor.YELLOW + "Current frame: " + frame);
		current.setAmount(frame);
		
		editor.setItem(PREV, prev);
		editor.setItem(CURRENT, current);
		editor.setItem(NEXT, next);
		
		editor.setItem(BACK, ItemUtils.createItem(Material.INK_SACK, "Back", 1));
		
		editor.setItem(P1I, ItemUtils.createItem(Material.WOOD_HOE, ChatColor.BLUE + "Player 1 Skull"));
		editor.setItem(P2I, ItemUtils.createItem(Material.STONE_HOE, ChatColor.BLUE + "Player 2 Skull"));
		editor.setItem(WINNER, ItemUtils.createItem(Material.DIAMOND_HOE, ChatColor.AQUA + "Winner Skull"));
		
		editor.setItem(CLONEPREV, ItemUtils.createItem(Material.PAPER, ChatColor.LIGHT_PURPLE + "Copy last frame"));
		
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
		if (!e.getInventory().getName().contains("Animation editor"))return;
		for (int i = 45; i <= 53; i++){
			if (e.getSlot() == i){
				e.setCancelled(true);
			}
		}
		Player p = (Player) e.getWhoClicked();
		
		int frameId = Integer.parseInt(e.getInventory().getName().split(" ")[3]);
		
		String animationName = e.getInventory().getName().split(" ")[2];
		Animation anim = AnimationsManager.getManager().getAnimation(animationName);
		
		if (e.getSlot() == NEXT){
			if (frameId == 50)return;
			this.saveFrame(anim, frameId, e.getInventory());
			p.openInventory(this.getEditor(anim, frameId+1));
			
		}
		if (e.getSlot() == PREV){
			if (frameId == 0 )return;
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
			for (int i = 0;i <= 44; i++){
				e.getInventory().setItem(i, anim.getFrame(frameId-1).getItem(i));
			}
		}
	}
	
	//Save on close
	@EventHandler
	public void closeSaver(InventoryCloseEvent e){
		if (!e.getInventory().getName().contains("Animation editor"))return;
		
		int frameId = Integer.parseInt(e.getInventory().getName().split(" ")[3]);
		
		String animationName = e.getInventory().getName().split(" ")[2];
		Animation anim = AnimationsManager.getManager().getAnimation(animationName);
		
		this.saveFrame(anim, frameId, e.getInventory());
	}
	
	
	//Name getter
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if (nameRetrieving.containsKey(e.getPlayer().getName())){
			e.setCancelled(true);
			String animation = e.getMessage().split(" ")[0];
			
			if (nameRetrieving.get(e.getPlayer().getName()) == 0){
				if (AnimationsManager.getManager().getAnimation(animation) != null){
					e.getPlayer().sendMessage(ChatColor.RED + "An animation with name " + animation + " already exists.");
					return;
				}
				AnimationsManager.getManager().createAnimation(animation);
				e.getPlayer().sendMessage(ChatColor.GREEN + "New animation with name " + animation + " generated.");
				
				
			}else{
				if (AnimationsManager.getManager().getAnimation(animation) == null){
					e.getPlayer().sendMessage(ChatColor.RED + "Animation with name " + animation + " doesn't exist.");
					return;
				}
				AnimationsManager.getManager().removeAnimation(animation);
				e.getPlayer().sendMessage(ChatColor.GREEN + "Animation " + animation + " deleted.");
			}
			
			
			nameRetrieving.remove(e.getPlayer().getName());
			
			
		}
	}
	
}
