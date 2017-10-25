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

        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        appl.clientsManager.forEach((client) -> {
            appl.getConsole().showModalInfo(String.format("%s %s", client, client.writeCfgFile(appl.logBuilder.getContent())));
        });
    }
}
