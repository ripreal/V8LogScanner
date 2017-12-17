package org.v8LogScanner.testV8LogScanner;

import org.junit.Test;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdScanner.V8LogScannerAppl;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.rgx.ScanProfile;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestClientsManager {

    @Test
    public void testWriteResultToFile() {

        ClientsManager manager = new ClientsManager();
        ScanProfile profile = new LanScanProfile();
        ScanProfile.buildUserEXCP(profile);
        manager.localClient().setProfile(profile);

        String fileName = fsys.createTempFile("", ".txt");
        manager.startRgxOp();
        manager.writeResultToFile(manager.localClient(), fileName, 100);

        assertEquals(true, new File(fileName).exists());
        fsys.deleteFile(fileName);
    }
}
