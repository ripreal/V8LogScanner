package org.v8LogScanner.cmdScanner;

import java.util.List;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Strokes;
import org.v8LogScanner.rgx.SelectorEntry;

public class CmdShowResults implements CmdCommand {
	
	private int count = 100;
	private boolean forward = true;
	
	public CmdShowResults(boolean _forward){
		forward = _forward;
	}
	
	public String getTip() {
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		if (appl.clientsManager.getClients().size() == 1) 
			return String.format("(curr. pos: %s)", appl.clientsManager.localClient().cursorIndex());
		else
			return "";
	}

	public void execute() {
		
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		
		List<V8LogScannerClient> clients = appl.clientsManager.getClients();
		
	// Choose server which events receive 
		String userInput = appl.getConsole().askInputFromList("input numeric index to choose server which events receive:", 
			clients);
			
		if (userInput == null)
			return;
	
		V8LogScannerClient userClient = clients.get(Integer.parseInt(userInput));
		
		List<SelectorEntry> selector = userClient.select(count, forward);
		
		// Choose key which list display 
		for(int i = selector.size() - 1; i >= 0; i--){
			appl.getConsole().println(
				String.format("\n%s. SIZE: %s,\n%s ", i, selector.get(i).size(), selector.get(i).getKey()));
		}
		
		String[] msg = new String[1];
		msg[0]= "\nInput numeric index to open the detail info about an event or any letter to exit:";

		userInput = "";
		do{
			userInput = appl.getConsole().askInput(msg, n -> true, false);
			
			if (!Strokes.isNumeric(userInput))
				break;
			
			int index = Integer.parseInt(userInput);
			if (index >= selector.size()){
				appl.getConsole().println("incorrect input!");
				continue;
			}
			
			List<String> keyVals = selector.get(index).getValue();
			appl.getConsole().showInfo(keyVals);
		}
		while(true);
	}
}
