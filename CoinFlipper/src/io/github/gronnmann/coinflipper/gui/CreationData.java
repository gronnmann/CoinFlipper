package io.github.gronnmann.coinflipper.gui;

import org.bukkit.inventory.Inventory;

public class CreationData {
	private Inventory gui;
	private int side;
	private double amount;
	
	public CreationData(Inventory gui){
		this.gui = gui;
		this.amount = 0;
		this.side = 0;
	}
	
	public int getSide(){
		return side;
	}
	public void setSide(int side){
		this.side = side;
	}
	public double getMoney(){
		return amount;
	}
	public void setMoney(double amount){
		this.amount = amount;
	}
	
	public Inventory getInventory(){
		return gui;
	}
	
}
