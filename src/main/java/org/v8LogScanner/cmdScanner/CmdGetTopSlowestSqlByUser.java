package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.ScanProfile;

public class CmdGetTopSlowestSqlByUser implements CmdCommand {

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        String userInput = appl.askUserName();
        if (userInput == null) {
            return;
        }
        ScanProfile.buildTopSlowestSqlByUser(appl.profile, userInput);
    }
}
