package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.Strokes;

public class CmdSetLogHistory implements CmdCommand {
    @Override
    public String getTip() {
        return "(" + V8LogScannerAppl.instance().logBuilder.getHistory() + ")";
    }

    @Override
    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String[] message = new String[1];
        message[0] = "Input integer number during that logs will be stored on a computer";
        String userInput = appl.getConsole().askInput(
                message,
                l_input -> Strokes.isNumeric(l_input),
                true
        );
        if (userInput == null) {
            return;
        }
        appl.logBuilder.setHistory(Integer.parseInt(userInput));
    }
}
