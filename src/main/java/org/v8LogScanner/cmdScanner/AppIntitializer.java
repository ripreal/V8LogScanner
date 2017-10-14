package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.commonly.ExcpReporting;

public class AppIntitializer {

    public static void main(String[] args) {

        ExcpReporting.out = System.out; // it is necessary to specify a output for errors before launch app
        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        appl.runAppl();

    }

}
