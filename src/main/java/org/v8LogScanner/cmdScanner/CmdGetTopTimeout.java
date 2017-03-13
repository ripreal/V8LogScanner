package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class CmdGetTopTimeout implements CmdCommand {

  @Override
  public String getTip() {
    return "";
  }

  @Override
  public void execute() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    appl.profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
    appl.clientsManager.localClient().setProfile(appl.profile);
    
    // Managed 1C lock
    RegExp ttimeout = new RegExp(EventTypes.TTIMEOUT);
    appl.profile.getRgxList().add(ttimeout);
    
    // DATABASE DBV8DBEng log
    //RegExp fileTimeout = new RegExp(EventTypes.EXCP);
    //Filter<String> fl = fileTimeout.getFilter(PropTypes.Descr);
    //fl.add("Конфликт блокировок при выполнении транзакции");
    //appl.profile.getRgxList().add(fileTimeout);
    
  }
}
