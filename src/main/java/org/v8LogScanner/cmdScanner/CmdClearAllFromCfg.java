package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdClearAllFromCfg implements CmdCommand {

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl.instance().logBuilder.clearAll();
    }
}
