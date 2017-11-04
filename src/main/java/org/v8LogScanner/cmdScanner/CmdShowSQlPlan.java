package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdShowSQlPlan implements CmdCommand {
    @Override
    public String getTip() {
        return "(" + Boolean.toString(V8LogScannerAppl.instance().logBuilder.getPlanSql()) + ")";
    }

    @Override
    public void execute() { V8LogScannerAppl.instance().logBuilder.setPlanSql(! V8LogScannerAppl.instance().logBuilder.getPlanSql());}
}
