package io.github.gronnmann.utils.coinflipper.input;

import java.util.ArrayList;
import java.util.HashMap;

public class InputData {
	

	public enum InputType {INTEGER, DOUBLE, STRING, TEXT, BOOLEAN, SOUND, MATERIAL}

	
	private double MIN = 0, MAX = 0;
	private boolean limitUp = false, limitDown = false;
	private InputType inputType;
	
	private HashMap<String, Object> extraData = new HashMap<String, Object>();
	
	
	
	private long createdMoment;
	
	private String id, exit = "exit";
	
	public InputData(String id, InputType type) {
		this.inputType = type;
		this.id = id;
		this.createdMoment = System.currentTimeMillis();
	}
	
	public InputData(String id, InputType type, String exitString) {
		this(id, type);
		this.exit = exitString;
	}
	
	public InputData(String id, InputType type, int min, int max) {
		this(id, type);
		
		this.MIN = min;
		this.MAX = max;
		this.limitUp = true;
		this.limitDown = true;
	}
	
	public InputData(String id, InputType type, String exitString, int min, int max) {
		this(id, type, min, max);
		this.exit = exitString;
	}
	
	public boolean hasMax() {
		return limitUp;
	}
	
	public boolean hasMin() {
		return limitDown;
	}
	
	public double getMax() {
		return MAX;
	}
	public double getMin() {
		return MIN;
	}
	
	public InputType getType() {
		return inputType;
	}
	
	public String getId() {
		return id;
	}
	
	public String getExitString() {
		return exit;
	}
	
	public long getCreated() {
		return createdMoment;
	}
	
	public void addExtraData(String key, Object data) {
		extraData.put(key, data);
	}
	
	public HashMap<String, Object> getExtraDataList(){
		return extraData;
	}
	
	public Object getExtraData(String key) {
		return extraData.get(key);
	}
}
