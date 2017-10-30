package org.v8LogScanner.commonly;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

public class ExcpReporting {

    public static PrintStream out;

    public static void LogError(Object cls, Exception e) {

        checkOut();

        Date currDate = new Date();

        String msg = String.format("ERROR, Time=%1$tF %2tT, Source=%3$2s, Class=%4$2s, Stack=truncated",
                currDate, currDate.getTime(), e.toString(), cls.toString());
        out.println(msg);
        e.printStackTrace(out);

        if (Constants.ALLOW_LOGGING) {
            try {
                String executionPath = System.getProperty("user.dir");
                fsys.writeInNewFile("\n" + msg, executionPath + "\\error_logs.txt", false);
            } catch (IOException excp) {
                throw new RuntimeException(excp);
            }
        }
    }

    public static void logInfo(String info) throws RuntimeException {

        checkOut();

        Date currDate = new Date();

        String msg = String.format("INFO, Time=%1$tF %2tT, %3$s",
                currDate, currDate.getTime(), info);
        out.println(msg);
    }

    private static void checkOut() throws ErrorStreamOutNotSet {
        if (out == null) {
            throw new ErrorStreamOutNotSet();
        }
    }

    public static class ErrorStreamOutNotSet extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public ErrorStreamOutNotSet() {
            super("Global output stream for printing errors is not defined! Set out stream forr error inside the ExcpReporting module");
        }

    }
}
