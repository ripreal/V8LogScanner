package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.commonly.ExcpReporting;

import java.util.regex.Pattern;

public class AppIntitializer {

    public static void main(String[] args) {
        ExcpReporting.out = System.out; // it is necessary to specify a output for errors before launch app
        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String pars = String.join(",", args).toUpperCase();
        if (pars.contains("SERVERMODE".toUpperCase()))
            appl.runAppl();
        else
            appl.runAppl();
    }
}
