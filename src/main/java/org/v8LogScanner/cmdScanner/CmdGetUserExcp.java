package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.ScanProfile;

public class CmdGetUserExcp implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        ScanProfile.buildUserEXCP(V8LogScannerAppl.instance().profile);
    }
}
