package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

import java.io.File;

public class CmdWriteToCfg implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl.instance().logBuilder.writeToXmlFile((info) -> {
            V8LogScannerAppl.instance().getConsole().showModalInfo(info[0]);
        });
    }
}
