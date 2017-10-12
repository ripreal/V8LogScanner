package org.v8LogScanner.cmdScanner;

import java.util.List;
import java.util.regex.Pattern;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class CmdGetTopSlowestSqlByUser implements CmdCommand {

  @Override
  public String getTip() {
    return "";
  }

  @Override
  public void execute() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();

    String[] msg = {"input user name (for example DefUser):"};
    String userInput = appl.getConsole().askInput(
      msg,
      (input) -> input.matches("\\S+"),
      true);
    if (userInput == null){
      return;
    }

    ScanProfile.buildTopSlowestSqlByUser(appl.profile, userInput);
  }
}
