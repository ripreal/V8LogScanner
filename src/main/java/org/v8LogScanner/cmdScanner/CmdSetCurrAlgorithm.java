package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class CmdSetCurrAlgorithm implements CmdCommand {

    private RgxOpTypes rgxOp;

    public CmdSetCurrAlgorithm(RgxOpTypes rgxOp) {
        this.rgxOp = rgxOp;
    }

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        appl.profile.setRgxOp(rgxOp);
    }
}
