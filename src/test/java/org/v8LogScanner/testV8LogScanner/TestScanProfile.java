package org.v8LogScanner.testV8LogScanner;

import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerData;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.logsCfg.LogBuilder;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

public class TestScanProfile {

    ScanProfile profile;

    @Before
    public void setup() {
        ExcpReporting.out = System.out;
        profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
    }

    @Test
    public void testAddLogPath() {
        profile.addLogPath("t:\testpath");
        profile.addLogPath("t:\testpath");
        assertEquals(profile.getLogPaths().size(), 1);
    }

    @Test
    public void testLanSerialization() {

        RegExp rgx = new RegExp(EventTypes.EXCP);
        rgx.getFilter(PropTypes.Time).add("23:24");
        rgx.getFilter(PropTypes.Descr).add("test");
        profile.addRegExp(rgx);

        SocketTemplates templates = SocketTemplates.instance();
        ServerSocket server = templates.createServerSocket(Constants.serverPort);
        assertNotNull(server);
        String ip = templates.getHostIP(server);
        Socket client = templates.createClientSocket(ip, Constants.serverPort);
        ObjectOutputStream stream = templates.getOutDataReader(client);
        boolean sent = templates.sendData(stream, profile);

        try {
            server.close();
        } catch (Exception e) {
        }

        assertTrue(sent);
    }

    @Test
    public void testSerialisationGetCfgPaths() {
        SocketTemplates templates = SocketTemplates.instance();
        ServerSocket server = templates.createServerSocket(Constants.serverPort);
        assertNotNull(server);

        String ip = templates.getHostIP(server);
        Socket client = templates.createClientSocket(ip, Constants.serverPort);

        V8LanLogScannerClient clientScanner = new V8LanLogScannerClient();

        V8LogScannerData dataToClient = new V8LogScannerData(V8LogScannerData.ScannerCommands.GET_CFG_PATHS);
        dataToClient.putData("cfgPaths", clientScanner.getCfgPaths());

        boolean sent = templates.sendData(templates.getOutDataReader(client), dataToClient);

        try {
            server.close();
        } catch (Exception e) {
        }

        assertTrue(sent);
    }

    @Test
    public void testSerialisationReadCfgFile() {
        SocketTemplates templates = SocketTemplates.instance();
        ServerSocket server = templates.createServerSocket(Constants.serverPort);
        assertNotNull(server);

        String ip = templates.getHostIP(server);
        Socket client = templates.createClientSocket(ip, Constants.serverPort);

        V8LanLogScannerClient clientScanner = new V8LanLogScannerClient();

        V8LogScannerData dataToClient = new V8LogScannerData(V8LogScannerData.ScannerCommands.READ_CFG_PATHS);
        dataToClient.putData("cfgContent", "test content");

        boolean sent = templates.sendData(templates.getOutDataReader(client), dataToClient);

        try {
            server.close();
        } catch (Exception e) {
        }
        assertTrue(sent);
    }
}
