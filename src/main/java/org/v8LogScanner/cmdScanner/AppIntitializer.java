package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class AppIntitializer {

    public static void main(String[] args) {

        ExcpReporting.out = System.out; // it is necessary to specify a output for errors before launch app

        String pars = String.join(",", args).toUpperCase();
        if (pars.contains("SERVER_SILENT".toUpperCase())) {
            startServerMode();
        } else if (pars.contains("SERVER".toUpperCase())) {
            CmdRunAsLanServer cmd = new CmdRunAsLanServer();
            cmd.execute();
        }
        else {
            V8LogScannerAppl.instance().runAppl();
        }
    }
    private static void startServerMode(){
        ByteBuffer bytes = Charset.forName("UTF-8").encode("");
        ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outStream);

        try {
            V8LogScannerServer server = new V8LogScannerServer(Constants.serverPort, out, in);
            server.Beginlistenning();
        } catch (V8LogScannerServer.LanServerNotStarted e) {
            ExcpReporting.LogError(AppIntitializer.class, e);
        }
    }
}
