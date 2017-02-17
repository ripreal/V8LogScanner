package org.v8LogScanner.rgx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectorEntry implements Serializable {
	
	private static final long serialVersionUID = -1029844113171114414L;
	private String key = "";
	private List<String> value = new ArrayList<>();
	
	public SelectorEntry(String key, List<String> value){
		this.key = key;
		this.value = value;
	}
	
	public String getKey(){
		return key;
	}
	
	public List<String> getValue(){
		return value;
	}
	
	public int size(){
		return value.size();
	}
	
	
	
}
