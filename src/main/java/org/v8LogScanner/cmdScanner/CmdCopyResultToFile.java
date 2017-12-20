package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.rgx.IRgxSelector;
import org.v8LogScanner.rgx.SelectorEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CmdCopyResultToFile implements CmdCommand {

    private int count = 100;

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        V8LogScannerClient userClient = appl.askServer();
        if (userClient == null)
            return;

        String fileName = fsys.createTempFile("", ".txt");
        appl.clientsManager.writeResultToFile(userClient, fileName, count);
        try {
            java.awt.Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            ExcpReporting.LogError(this, e);
            fsys.deleteFile(fileName);
        }
    }
}
