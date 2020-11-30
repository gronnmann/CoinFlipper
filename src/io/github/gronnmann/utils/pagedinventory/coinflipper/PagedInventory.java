package io.github.gronnmann.utils.pagedinventory.coinflipper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.utils.coinflipper.Debug;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import io.github.gronnmann.utils.coinflipper.ReflectionUtils;

public class PagedInventory implements Inventory{
	
	private static ArrayList<PagedInventory> pagedInventories = new ArrayList<PagedInventory>();
	
	public static PagedInventory getByInventory(Inventory inv){
		for (PagedInventory pInv : pagedInventories){
			if (pInv.containsInventory(inv)){
				return pInv;
			}
		}
		return null;
	}
	
	
	
	private HashMap<Integer, Inventory> invs = new HashMap<Integer, Inventory>();
	private Inventory copyFrom;
	private String id;
	protected Inventory redirectToBack;
	
	public static int NEXT = 50, PREV = 48, CURRENT = 49, BACK = 45;
	
	public static int usableSlots = 45;
	
	public PagedInventory(String name, ItemStack next, ItemStack last, ItemStack back, String id, Inventory redirectToBack){
		
		this.id = id;
		this.redirectToBack = redirectToBack;
		
		pagedInventories.add(this);
		
		copyFrom = Bukkit.createInventory(new PagedInventoryHolder(), 54, name);
		
		copyFrom.setItem(NEXT, next);
		copyFrom.setItem(PREV, last);
		
		copyFrom.setItem(CURRENT, ItemUtils.createItem(Material.THIN_GLASS, "0"));
		
		copyFrom.setItem(BACK, back);
		
		addPage();
	}
	
	public String getId(){
		return id;
	}
	
	public int sizePages(){
		return invs.size();
	}
	
	public int addPage(){
		
		int count = sizePages();
		
		Inventory nextPage = Bukkit.createInventory(copyFrom.getHolder(), copyFrom.getSize(), ReflectionUtils.getInventoryName(copyFrom));
		
		nextPage.setContents(copyFrom.getContents());
		
		int preview  = count;
		if (preview > 64){
			preview = 64;
		}
		if (preview < 0){
			preview = 1;
		}
		
		ItemStack current = new ItemStack(Material.THIN_GLASS, preview);
		ItemUtils.setName(current, ChatColor.YELLOW.toString() + count);
		
		nextPage.setItem(CURRENT, current);
		
		invs.put(count, nextPage);
		
		
		
		return count;
	}
	
	public ArrayList<Inventory> getPages(){
		ArrayList<Inventory> pages = new ArrayList<Inventory>();
		for (Inventory inv : invs.values()){
			pages.add(inv);
		}
		return pages;
	}
	
	public Inventory getPage(int num){
		return invs.get(num);
	}
	
	public int getNumber(Inventory inv){
		for (int num : invs.keySet()){
			if (invs.get(num).equals(inv)){
				return num;
			}
		}
		return -1;
	}
	
	
	public boolean containsInventory(Inventory inv){
		for (Inventory toCompare : invs.values()){
			if (toCompare.equals(inv))return true;
		}
		return false;
	}
	
	
	
	public void setReturnInventory(Inventory inv) {
		this.redirectToBack = inv;
	}
	
	public static PagedInventory fromClone(PagedInventory inv, String name) {
		PagedInventory clone = new PagedInventory(name, inv.getPage(0).getItem(NEXT), inv.getPage(0).getItem(PREV), 
				inv.getPage(0).getItem(BACK), inv.getId(), inv.redirectToBack);
		Debug.print(clone.toString());
		clone.setContents(inv.getContents());
		Debug.print(clone.getPages().toString());
		
		return clone;
	}
	
	public HashMap<Integer, ItemStack> addItem(ItemStack... arg0) throws IllegalArgumentException {
		for (int invNumbers : invs.keySet()){
			Inventory inv = invs.get(invNumbers);
			for (int i = 0; i < usableSlots; i++){
				ItemStack item = inv.getItem(i);
				
				if (item == null || item.getType().equals(Material.AIR)){
					inv.addItem(arg0);
					return null;
				}
			}
		}
		
		int newP = addPage();
		invs.get(newP).addItem(arg0);
		
		return null;
	}

	
	public HashMap<Integer, ? extends ItemStack> all(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public HashMap<Integer, ? extends ItemStack> all(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	
	public void clear(int arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean contains(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean contains(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean contains(ItemStack arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean contains(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean contains(Material arg0, int arg1) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean contains(ItemStack arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean containsAtLeast(ItemStack arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int first(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int first(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int first(ItemStack arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int firstEmpty() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public ItemStack[] getContents() {
		ArrayList<ItemStack> contents = new ArrayList<ItemStack>();
		for (int invNumbers : invs.keySet()) {
			Inventory inv = invs.get(invNumbers);
			for (int i = 0; i < usableSlots; i++) {
				ItemStack item = inv.getItem(i);
				if (item != null) {
					contents.add(item);
				}
				
			}
		}
		
		return contents.toArray(new ItemStack[0]);
	}

	
	public InventoryHolder getHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ItemStack getItem(int arg0) {
		int invToUse = arg0/usableSlots;
		
		int slotToUse = arg0-invToUse*usableSlots;
		
		return invs.get(invToUse).getItem(slotToUse);
	}

	
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getMaxStackSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public String getName() {
		return ReflectionUtils.getInventoryName(copyFrom);
	}

	
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public ItemStack[] getStorageContents() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getTitle() {
		return ReflectionUtils.getInventoryName(copyFrom);
	}

	
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	
	public List<HumanEntity> getViewers() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ListIterator<ItemStack> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ListIterator<ItemStack> iterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void remove(int arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void remove(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	
	public void remove(ItemStack arg0) {
		for (Inventory inv : invs.values()){
			if (inv.contains(arg0)){
				inv.remove(arg0);
			}
		}
		
	}

	
	public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setContents(ItemStack[] arg0) throws IllegalArgumentException {
		for (Inventory inv : invs.values()){
			for (int i = 0; i < usableSlots; i++){
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}
		
		
		for (ItemStack i : arg0) {
			this.addItem(i);
		}
		
	}

	
	public void setItem(int arg0, ItemStack arg1) {
		int invToUse = arg0/usableSlots;
		
		int slotToUse = arg0-invToUse*usableSlots;
		
		
		invs.get(invToUse).setItem(slotToUse, arg1);
		
	}

	
	public void setMaxStackSize(int arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void setStorageContents(ItemStack[] arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	public boolean isEmpty() {
		return false;
	}

}

class PagedInventoryHolder implements InventoryHolder{

	
	public Inventory getInventory() {
		return null;
	}
	
}