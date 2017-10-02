package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdAddNonEffectiveQueriesEvent implements CmdCommand{
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String[] msg = {"input 1c user name that actions you're looking for:"};
        /*
        String input = appl.getConsole().askInput(msg, (text) -> {
            boolean res = text.matches("\\S+");
            return res;
        });

        if (input == null)
            return;

        appl.logBuilder.buildInvestigateNonEffectiveQueries()
        */
    }
}
