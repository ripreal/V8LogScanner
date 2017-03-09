package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdStartCursorOp implements CmdCommand {

  public String getTip() {
    return "";
  }

  public void execute() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    appl.startRgxOP();
  }
}
