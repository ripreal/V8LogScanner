package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Constants;

public class CmdAllowLogging implements CmdCommand {
    @Override
    public String getTip() {
        return String.format("(%s)", Boolean.toString(Constants.ALLOW_LOGGING));
    }

    @Override
    public void execute() {
        Constants.ALLOW_LOGGING = !Constants.ALLOW_LOGGING;
    }
}
