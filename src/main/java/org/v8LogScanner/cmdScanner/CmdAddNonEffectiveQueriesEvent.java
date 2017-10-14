package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdAddNonEffectiveQueriesEvent implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String[] msg = {"input a 1c user name which actions you're looking for (CAN BE OMITTED):"};
        String userName = appl.getConsole().askInput(msg,
                (text) -> text.matches("\\S+"),
                true);

        String[] msg2 = {"input a 1c database name  as it's indicated within connection string (CAN BE OMITTED):"};
        String baseName = appl.getConsole().askInput(msg2,
                (text) -> text.matches("\\S+"),
                true);

        appl.logBuilder.buildInvestigateSQlQueries(userName, baseName);
    }
}
