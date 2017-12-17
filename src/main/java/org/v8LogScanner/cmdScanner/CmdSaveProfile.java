package org.v8LogScanner.cmdScanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.CmdCommand;

import java.io.File;
import java.io.IOException;

public class CmdSaveProfile implements CmdCommand {


    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String[] message = {String.format("Input desired profile name (\"%s\" by default)", appl.profile.getName())};
        String profileName = appl.getConsole().askInput(
                message, (input) -> true, true, true);

        if (profileName != null && !profileName.isEmpty())
            appl.profile.setName(profileName);

        try {
            appl.clientsManager.saveProfile();
        } catch (IOException e) {
            appl.getConsole().showModalInfo(e.getMessage());
        }
    }
}
