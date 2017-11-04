package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdCollectCircR implements CmdCommand {
    @Override
    public String getTip() {
        return "(" + Boolean.toString(V8LogScannerAppl.instance().logBuilder.getScriptcircrefs()) + ")";
    }

    @Override
    public void execute() { V8LogScannerAppl.instance().logBuilder.setScriptcircrefs(! V8LogScannerAppl.instance().logBuilder.getScriptcircrefs());}
}
