package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.ScanProfile.LogTypes;

public class CmdChangeLogType implements CmdCommand {
	
	public String getTip() {
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		return  String.format("[%s]", appl.profile.getLogType());
	}

	public void execute() {
		
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		
		LogTypes[] logTypes = LogTypes.values();
		
		String userInput = appl.cmdAppl.askInputFromList("choose log type used to filter log files", logTypes);
		
		if (userInput == null)
			return;
		
		appl.profile.setLogType(logTypes[Integer.parseInt(userInput)]);
	}
}
