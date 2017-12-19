package io.github.gronnmann.utils.pagedinventory.coinflipper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import io.github.gronnmann.coinflipper.MessagesManager;
import io.github.gronnmann.coinflipper.MessagesManager.Message;
import io.github.gronnmann.utils.coinflipper.ItemUtils;
import net.md_5.bungee.api.ChatColor;

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
	
	private static int usableSlots = 45;
	
	public PagedInventory(String name, ItemStack next, ItemStack last, ItemStack back, String id, Inventory redirectToBack){
		
		this.id = id;
		this.redirectToBack = redirectToBack;
		
		pagedInventories.add(this);
		
		copyFrom = Bukkit.createInventory(new PagedInventoryHolder(), 54, name);
		
		copyFrom.setItem(NEXT, next);
		copyFrom.setItem(PREV, last);
		
		copyFrom.setItem(CURRENT, ItemUtils.createItem(Material.THIN_GLASS, "0"));
		
		copyFrom.setItem(BACK, back);
	}
	
	public String getId(){
		return id;
	}
	
	public int sizePages(){
		return invs.size();
	}
	
	public int addPage(){
		
		int count = sizePages();
		
		Inventory nextPage = Bukkit.createInventory(copyFrom.getHolder(), copyFrom.getSize(), copyFrom.getName());
		
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
	
	
	

	@Override
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

	@Override
	public HashMap<Integer, ? extends ItemStack> all(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(ItemStack arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Material arg0, int arg1) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(ItemStack arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAtLeast(ItemStack arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int first(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int first(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int first(ItemStack arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int firstEmpty() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack[] getContents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InventoryHolder getHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getItem(int arg0) {
		int invToUse = arg0/usableSlots;
		
		int slotToUse = arg0-invToUse*usableSlots;
		
		return invs.get(invToUse).getItem(slotToUse);
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxStackSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return copyFrom.getName();
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack[] getStorageContents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return copyFrom.getTitle();
	}

	@Override
	public InventoryType getType() {
		return InventoryType.CHEST;
	}

	@Override
	public List<HumanEntity> getViewers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<ItemStack> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<ItemStack> iterator(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Material arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(ItemStack arg0) {
		for (Inventory inv : invs.values()){
			if (inv.contains(arg0)){
				inv.remove(arg0);
			}
		}
		
	}

	@Override
	public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContents(ItemStack[] arg0) throws IllegalArgumentException {
		for (Inventory inv : invs.values()){
			inv.clear();
		}
		for (ItemStack item : arg0){
			this.addItem(item);
		}
		
	}

	@Override
	public void setItem(int arg0, ItemStack arg1) {
		int invToUse = arg0/usableSlots;
		
		int slotToUse = arg0-invToUse*usableSlots;
		
		invs.get(invToUse).setItem(slotToUse, arg1);
		
	}

	@Override
	public void setMaxStackSize(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setStorageContents(ItemStack[] arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

}

class PagedInventoryHolder implements InventoryHolder{

	@Override
	public Inventory getInventory() {
		return null;
	}
	
}