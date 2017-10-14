package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.Strokes;

public class CmdSetTOP implements CmdCommand {

    @Override
    public String getTip() {
        Number top = V8LogScannerAppl.instance().profile.getLimit();
        return "[" + top.toString() + "]";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String[] message = new String[1];
        message[0] = "Input integer number of events returned from each log file per every " + Constants.logEventsCount + " events."
                + "\nThis option only works for the cursor log scanning:";
        String userInput = appl.getConsole().askInput(
                message,
                l_input -> Strokes.isNumeric(l_input),
                true
        );
        if (userInput == null) {
            return;
        }
        appl.profile.setLimit(Math.min(Integer.parseInt(userInput), Constants.logEventsCount));
    }
}
