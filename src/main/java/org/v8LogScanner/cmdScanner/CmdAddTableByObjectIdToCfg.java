package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdAddTableByObjectIdToCfg implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        String[] msg = {"input a ms sql OBJECT_ID table name which events you're looking for:"};
        String objectID = appl.getConsole().askInput(msg,
                (text) ->  text.matches("\\S+"),
                true);

        if (objectID == null)
            return;

        V8LogScannerAppl.instance().logBuilder.buildFindTableByObjectID(objectID);
    }
}
