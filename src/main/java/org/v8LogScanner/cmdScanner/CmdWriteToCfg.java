package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.AppPolicy;

public class CmdWriteToCfg implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        AppPolicy.addAdministratorRights();
        V8LogScannerAppl.instance().logBuilder.writeToXmlFile((info) -> {
            V8LogScannerAppl.instance().getConsole().showModalInfo(info[0]);
        });
    }
}
