package org.v8LogScanner.cmdScanner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdAddLogLocFromCfgAll implements CmdCommand {

  @Override
  public String getTip() {
    return "";
  }

  @Override
  public void execute() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    appl.getConsole().println("");
    
    Map<V8LogScannerClient, List<String>> allLogs = new HashMap<>();
    appl.clientsManager.forEach(client -> allLogs.put(client, client.scanLogsInCfgFile()));
    
    for (V8LogScannerClient client : allLogs.keySet()){
      for(String log : allLogs.get(client))
        appl.addLogPath(client.getProfile(), log);
    }
  }
}
