package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.ScanProfile;

public class CmdGetSQlWithIndexScan implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    public void execute() {
        ScanProfile.BuildFindSlowSQlEventsWithPlan(V8LogScannerAppl.instance().profile, "Index scan");
    }
}
