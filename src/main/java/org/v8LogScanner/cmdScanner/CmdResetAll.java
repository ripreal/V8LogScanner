package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.ScanProfile;

public class CmdResetAll implements CmdCommand{

  public String getTip() {
    return "";
  }

  public void execute() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    appl.clientsManager.reset();
    ScanProfile.RgxOpTypes opType = appl.profile.getRgxOp();
    appl.profile = appl.clientsManager.localClient().getProfile();
    appl.profile.setRgxOp(opType);
    appl.profile.addRegExp(new RegExp());
  }
}
