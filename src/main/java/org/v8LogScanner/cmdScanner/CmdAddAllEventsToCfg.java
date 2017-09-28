package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdAddAllEventsToCfg implements CmdCommand {
    @Override
    public String getTip() {
        return null;
    }

    @Override
    public void execute() {
        V8LogScannerAppl.instance().logBuilder.buildAllEvents();
    }
}
