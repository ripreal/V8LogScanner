package org.v8LogScanner.LocalTCPLogScanner;

import java.io.Serializable;
import java.util.HashMap;

public class V8LogScannerData implements Serializable{
	
	private static final long serialVersionUID = 5227705164357637837L;

	public enum ScannerCommands {GET_LOG_FILES_FROM_CFG, EXECUTE_RGX_OP, SELECT_RGX_RESULT, 
		GET_PROC_INFO, GET_RGX_INFO, PATH_EXIST, RESET_RESULT, RESET_ALL};
	
	public final ScannerCommands command;
	private final HashMap<String, Object> data = new HashMap<>();
	
	public V8LogScannerData(ScannerCommands command){
		this.command = command;
	}
	
	public void putData(String key, Object value){
		data.put(key, value);
	}
	
	public Object getData(String key){
		return data.get(key);
	}
	
}
