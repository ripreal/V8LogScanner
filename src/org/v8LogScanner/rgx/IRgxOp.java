package org.v8LogScanner.rgx;

import java.util.ArrayList;
import java.util.List;

import org.v8LogScanner.commonly.PerfCalc;
import org.v8LogScanner.commonly.ProcessEvent;

public interface IRgxOp{
	
	public final PerfCalc calc = new PerfCalc();
	
	/** 
	 * @param parameterObject
	 */
	public void execute(List<String> logFiles);
	
	public ArrayList<String> getProcessingInfo();
		
	public String getFinalInfo(String logDescr);
	
	public List<SelectorEntry> select(int count, boolean forward);
	
	public int cursorIndex();
	
	public void addListener(ProcessEvent e);
	
}
