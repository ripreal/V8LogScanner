package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile.LogTypes;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class CmdGetRphostExcp implements CmdCommand {

  public String getTip() {
    return "";
  }
  
  public void execute() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    appl.profile = new LanScanProfile(RgxOpTypes.HEAP_OP);
    appl.clientsManager.localClient().setProfile(appl.profile);
    
    RegExp excp = new RegExp(EventTypes.EXCP);
    excp.getGroupingProps().add(PropTypes.Descr);
    
    appl.profile.getRgxList().add(excp);
    appl.profile.setLogType(LogTypes.RPHOST);
    
  }
}
