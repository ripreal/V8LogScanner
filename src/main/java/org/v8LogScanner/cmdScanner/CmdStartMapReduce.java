/////////////////////////////////////
//COMMAND PATTERN

package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdStartMapReduce implements CmdCommand {

    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        appl.startRgxOP();
    }

    public String getTip() {
        return "";
    }
}
