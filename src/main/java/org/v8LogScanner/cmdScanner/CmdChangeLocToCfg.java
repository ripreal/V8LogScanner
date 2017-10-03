package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer.LanServerNotStarted;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.Strokes;

public class CmdChangeLocToCfg implements CmdCommand{

  @Override
  public String getTip() {
    return String.format("(%s)", String.join(
      "; ", V8LogScannerAppl.instance().logBuilder.getLocations()
    ));
  }

  @Override
  public void execute() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();

    String[] msg_log = {"input path for uploading .*log files (must be correct for all servers): "};
    String path = appl.getConsole().askInput(msg_log, (text) -> !text.isEmpty(), true);

    if (path == null) {
      return;
    }

    String[] msg_history = {"input history period during that log files are remained on the server(integer number): "};
    String history = appl.getConsole().askInput(msg_history, (text) -> Strokes.isNumeric(text), true);

    if (history == null) {
      return;
    }
    appl.logBuilder.addLocLocation(path, history);
  }
}
