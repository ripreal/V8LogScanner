package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer.LanServerNotStarted;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;

public class CmdRunAsLanServer implements CmdCommand{

	@Override
	public String getTip() {
		return "";
	}

	@Override
	public void execute() {
		
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		
		try {
			appl.getConsole().clearConsole();
			V8LogScannerServer server = new V8LogScannerServer(Constants.serverPort);
			server.Beginlistenning();
		} catch (LanServerNotStarted e) {
			// TODO Auto-generated catch block
			ExcpReporting.LogError(this, e);
			String[] msg = {"input any symbol for continuing"};
			appl.getConsole().askInput(msg, n -> true, false);
		}
	}

}
