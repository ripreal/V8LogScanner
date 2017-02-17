package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdResetLoc implements CmdCommand {

	@Override
	public String getTip() {
		return "";
	}

	@Override
	public void execute() {
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		appl.clientsManager.resetRemoteClients();
		appl.profile.getLogPaths().clear();
	}
}
