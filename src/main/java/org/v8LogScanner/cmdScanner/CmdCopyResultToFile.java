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

        List<SelectorEntry> selector = userClient.select(count, IRgxSelector.SelectDirections.FORWARD);

        String fileName = fsys.createTempFile("", ".txt");
        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < selector.size(); i++) {
                SelectorEntry entry = selector.get(i);
                writer.write(String.format("\n%s. SIZE: %s,\n%s \n", i, selector.get(i).size(), selector.get(i).getKey()));
                writer.write(String.join("\n", entry.getValue()));
                writer.write("\n");
                writer.write("///////NEXT KEY//////////");
            }
            java.awt.Desktop.getDesktop().open(new File(fileName));
        } catch (IOException e) {
            ExcpReporting.LogError(this, e);
            fsys.deleteFile(fileName);
        }
    }
}
