package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.rgx.IRgxSelector;
import org.v8LogScanner.rgx.SelectorEntry;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

public class AppIntitializer {

    public static void main(String[] args) {

        String t = "D:\\temp\\storage1c";

        ExcpReporting.out = System.out; // it is necessary to specify a output for errors before launch app

        String pars = String.join(",", args).toUpperCase();
        if (pars.contains("SERVICE".toUpperCase())) {
            startService(args);
        } else if (pars.contains("SERVER".toUpperCase())) {
            CmdRunAsLanServer cmd = new CmdRunAsLanServer();
            cmd.execute();
        }
        else if (pars.contains("EXEC")) {
            execProfile();
        }
        else {
            V8LogScannerAppl appl = V8LogScannerAppl.instance();
            appl.loadProfile();
            appl.runAppl();
        }
    }

    private static void startService(String[] args){
        try {
            V8LogScannerServer server = new V8LogScannerServer(Constants.serverPort, null, null);
            server.Beginlistenning();
        } catch (V8LogScannerServer.LanServerNotStarted e) {
            ExcpReporting.LogError(AppIntitializer.class, e);
        }
    }

    private static void execProfile() {
        ClientsManager manager = new ClientsManager();
        try {
            manager.loadProfile();
            manager.startRgxOp();
            manager.writeResultToFile(manager.localClient(), "v8LogScanner_result.txt", 100);
        } catch (IOException e) {
            ExcpReporting.LogError(AppIntitializer.class, e);
        }
    }
}
