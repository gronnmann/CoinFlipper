package io.github.gronnmann.coinflipper.customizable;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.gronnmann.coinflipper.ConfigManager;

public enum CustomMaterial {
	
	BACK("back", Material.INK_SACK, 1),
	SELECTION_FILLING("selection_filling", Material.STAINED_GLASS_PANE, 10),
	SELECTION_CREATE("selection_create", Material.STAINED_GLASS_PANE, 5),
	SELECTION_SYNTAX("selection_syntax", Material.BOOK, 0),
	
	CREATION_MONEY("creation_money", Material.EMERALD, 0),
	CREATION_MONEY_VALUE("creation_money_value", Material.EMERALD, 0),
	CREATION_SIDE_HEADS("creation_side_heads", Material.WOOL, 15),
	CREATION_SIDE_TAILS("creation_side_tails", Material.WOOL, 0)
	;
	
	private String path;
	private Material mat, defaultMat;
	private int data, defaultData;
	
	CustomMaterial(String path, Material mat, int data){
		this.path = path;
		
		this.mat = mat;
		this.defaultMat = mat;
		
		this.data = data;
		this.defaultData = data;
	}
	
	public String getPath() {
		return path;
	}
	
	public Material getMaterial() {
		return mat;
	}
	public int getData() {
		return data;
	}
	public void setMaterial(Material mat) {
		this.mat = mat;
		save(true, false);
	}
	public void setData(int data) {
		this.data = data;
		save(false, true);
	}
	
	public void load() {
		FileConfiguration materials = ConfigManager.getManager().getMaterials();
		
		if (materials == null)return;
		
		if (materials.getString(path) != null) {
			try {
				mat = Material.valueOf(materials.getString(path));
			}catch(Exception e) {mat = defaultMat;}
		}else {
			setMaterial(defaultMat);
		}
		
		if (materials.get(path+"_data") != null) {
			data = materials.getInt(path+"_data", 0);
		}else {
			setData(defaultData);
		}
		
	}
	
	public void save(boolean saveMaterial, boolean saveData) {
		FileConfiguration materials = ConfigManager.getManager().getMaterials();
		
		if (saveMaterial) {
			materials.set(path, mat.toString());
		}
		if (saveData) {
			materials.set(path+"_data", data);
		}
		
		ConfigManager.getManager().saveMaterials();
		
		
	}
	
	public static CustomMaterial fromPath(String path) {
		for (CustomMaterial var : CustomMaterial.values()) {
			if (var.getPath().equals(path))return var;
		}
		
		return null;
	}
	
}
