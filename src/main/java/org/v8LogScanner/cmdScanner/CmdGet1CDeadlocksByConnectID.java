package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.ScanProfile;

public class CmdGet1CDeadlocksByConnectID implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        String[] msg = {"input user connect ID(for example 14):"};
        String connectID = appl.getConsole().askInput(
                msg,
                (input) -> input.matches("\\d+"),
                true
        );
        if (connectID == null)
            return;
        ScanProfile.build1cDeadlocksByConnectID(appl.profile, connectID);
    }
}
