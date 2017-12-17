package org.v8LogScanner.testV8LogScanner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager.LogScannerClientNotFoundServer;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer.LanServerNotStarted;
import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.MenuCmd;
import org.v8LogScanner.cmdAppl.MenuItemCmd;
import org.v8LogScanner.cmdScanner.CmdSaveProfile;
import org.v8LogScanner.cmdScanner.V8LogScannerAppl;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.testV8LogScanner.V8LogFileConstructor.LogFileTypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TestCmdAppl {

    private String logFileName = "";

    @Before
    public void setup() {

        ExcpReporting.out = System.out;

        V8LogFileConstructor constructor = new V8LogFileConstructor();
        logFileName = constructor
                .addEXCP()
                .build(LogFileTypes.FILE);
    }

    @Test
    public void testExcpReporting() throws LogScannerClientNotFoundServer {

        ExcpReporting.out = null;

        V8LanLogScannerClient client = new V8LanLogScannerClient();
        try {
            client.cursorIndex();
            fail("should throw ErrorStreamOutNotSet exception");
        } catch (ExcpReporting.ErrorStreamOutNotSet e) {
            assertTrue(true);
        }
    }

    @Test
    public void testRunAppl() throws IOException {

        //out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        //in
        ByteBuffer bytes = Charset.forName("UTF-8").encode("1"); // select first menu item
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        ApplConsole appl = new ApplConsole(in, out);

        // Test click on menu item

        MenuCmd mainMenu = new MenuCmd("main", null);
        mainMenu.add(new MenuItemCmd("first item", null));

        appl.runAppl(mainMenu);
        byte[] outBytes = outStream.toByteArray();
        assertTrue(outBytes.length != 0);
    }

    @Test
    public void testStartCursorOp() throws LogScannerClientNotFoundServer, LanServerNotStarted {

        //out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        //in
        StringBuilder sb = new StringBuilder();
        sb.append("1");   //1. Cursor log scanning(recommends)
        sb.append("\n1"); //1. SELECT TOP[100] FROM location[0]
        sb.append("\n3"); //3. Add own log location
        sb.append(String.format("\n%s", logFileName)); // Here we print log location
        sb.append("\nq"); //9. Start
        sb.append("\n");
        ByteBuffer bytes = Charset.forName("UTF-8").encode(sb.toString());
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        ApplConsole appl = new ApplConsole(in, out);
        V8LogScannerAppl scannerAppl = V8LogScannerAppl.instance();
        // cancel reset client so that it did not clear ScanProfile
        ClientsManager manager = new ClientsManager();
        ClientsManager mockedManager = Mockito.spy(manager);
        Mockito.doNothing().when(mockedManager).resetRemoteClients();
        scannerAppl.setApplConsole(appl, mockedManager);

        scannerAppl.runAppl();

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testCmdSetCurAlgorithm() {

        //out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        //in
        StringBuilder sb = new StringBuilder();
        sb.append("1");   //1. Cursor log scanning(recommends)
        sb.append("\n1"); //1. SELECT TOP[100] FROM location[0]
        sb.append("\n3"); //3. Add own log location
        sb.append(String.format("\n%s", logFileName)); // Here we print log location
        sb.append("\n9"); //9. Start
        sb.append("\nq"); //3. Exit
        sb.append("\nq"); //10. Exit
        sb.append("\n2"); // Heap log scanning
        sb.append("\n");
        ByteBuffer bytes = Charset.forName("UTF-8").encode(sb.toString());
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        ApplConsole appl = new ApplConsole(in, out);
        V8LogScannerAppl scannerAppl = V8LogScannerAppl.instance();

        // cancel reset client so that it did not clear ScanProfile
        ClientsManager manager = new ClientsManager();
        ClientsManager mockedManager = Mockito.spy(manager);
        Mockito.doNothing().when(mockedManager).resetRemoteClients();
        scannerAppl.setApplConsole(appl, mockedManager);

        scannerAppl.runAppl();

        assertEquals(ScanProfile.RgxOpTypes.HEAP_OP, scannerAppl.profile.getRgxOp());

        V8LogFileConstructor.deleteLogFile(logFileName);

    }

    @Test
    public void testCmdChangeLogType() {

        //out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        //in
        StringBuilder sb = new StringBuilder();
        sb.append("1");   //1. Cursor log scanning(recommends)
        sb.append("\n1"); //1. SELECT TOP[100] FROM location[0]
        sb.append("\n3"); //3. Add own log location
        sb.append(String.format("\n%s", logFileName)); // Here we print log location
        sb.append("\n2"); //2. WHERE log type in
        sb.append("\n0"); //0. RPHOST
        sb.append("\n9"); //9. Start
        sb.append("\nq"); //3. Exit
        sb.append("\n2"); //2. WHERE log type in
        sb.append("\n1"); //1. CLIENT
        sb.append("\n9"); //9. Start
        sb.append("\nq"); //3. Exit
        sb.append("\n");
        ByteBuffer bytes = Charset.forName("UTF-8").encode(sb.toString());
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        ApplConsole appl = new ApplConsole(in, out);
        V8LogScannerAppl scannerAppl = V8LogScannerAppl.instance();
        // cancel reset client so that it did not clear ScanProfile
        ClientsManager manager = new ClientsManager();
        ClientsManager mockedManager = Mockito.spy(manager);
        Mockito.doNothing().when(mockedManager).resetRemoteClients();

        scannerAppl.setApplConsole(appl, mockedManager);
        scannerAppl.runAppl();

        assertEquals(ScanProfile.LogTypes.CLIENT, scannerAppl.profile.getLogType());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testCmdSaveProfile() {
        //out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        //in
        StringBuilder sb = new StringBuilder();
        sb.append("6");   //6. Other
        sb.append("\n3"); //3. Save profile on disk
        sb.append("\n"); // choose default name
        sb.append("\n");
        ByteBuffer bytes = Charset.forName("UTF-8").encode(sb.toString());
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        V8LogScannerAppl scannerAppl = V8LogScannerAppl.instance();
        ApplConsole appl = new ApplConsole(in, out);

        String tempProfileFile = "tempProfilefile_save.json";
        scannerAppl.setProfileFileName(tempProfileFile);
        scannerAppl.setApplConsole(appl);
        scannerAppl.runAppl();

        assertEquals(true, Files.exists(Paths.get(tempProfileFile)));

        V8LogFileConstructor.deleteLogFile(logFileName);
        V8LogFileConstructor.deleteLogFile(tempProfileFile);
    }

    @Test
    public void testCmdLoadProfile() {
        //out
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);
        //in
        StringBuilder sb = new StringBuilder();
        sb.append("6");   //6. Other
        sb.append("\n3"); //3. Save profile on disk
        sb.append("\n"); // choose default name
        sb.append("\n4"); //4. Load profile from disk
        sb.append("\n");
        ByteBuffer bytes = Charset.forName("UTF-8").encode(sb.toString());
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        ApplConsole appl = new ApplConsole(in, out);
        V8LogScannerAppl scannerAppl = V8LogScannerAppl.instance();

        String tempProfileFile = "tempProfilefile_load.json";

        ScanProfile.buildSlowestEventsByUser(scannerAppl.profile, "temp");

        String profileNameBefore = scannerAppl.profile.getName();
        int eventsAmountBefore = scannerAppl.profile.getRgxList().size();

        scannerAppl.setProfileFileName(tempProfileFile);

        scannerAppl.setApplConsole(appl);
        scannerAppl.runAppl();

        assertEquals(profileNameBefore, scannerAppl.profile.getName());
        assertEquals(eventsAmountBefore, scannerAppl.profile.getRgxList().size());

        V8LogFileConstructor.deleteLogFile(logFileName);
        V8LogFileConstructor.deleteLogFile(tempProfileFile);
    }
}

