package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class AppIntitializer {

    public static void main(String[] args) {

        ExcpReporting.out = System.out; // it is necessary to specify a output for errors before launch app

        String pars = String.join(",", args).toUpperCase();
        if (pars.contains("SERVICE".toUpperCase())) {
            startService(args);
        } else if (pars.contains("SERVER".toUpperCase())) {
            CmdRunAsLanServer cmd = new CmdRunAsLanServer();
            cmd.execute();
        }
        else {
            V8LogScannerAppl.instance().runAppl();
        }
    }

    public static void startService(String[] args){
        try {
            V8LogScannerServer server = new V8LogScannerServer(Constants.serverPort, null, null);
            server.Beginlistenning();
        } catch (V8LogScannerServer.LanServerNotStarted e) {
            ExcpReporting.LogError(AppIntitializer.class, e);
        }
    }
}
