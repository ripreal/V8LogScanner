package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdResetResult implements CmdCommand {

	@Override
	public String getTip() {
		return "";
	}

	@Override
	public void execute() {
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		appl.clientsManager.forEach(client -> client.resetResult());
	}
}
