package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.logsCfg.LogEvent;

import java.util.List;

public class CmdRemoveEventFromCfg implements CmdCommand {
    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {
        V8LogScannerAppl appl = V8LogScannerAppl.instance();
        List<LogEvent> events = appl.logBuilder.getLogEvents();
        String eventIndex = appl.getConsole().askInputFromList("Input prop:", events);
        if (eventIndex == null)
            return;
        LogEvent logEvent = events.get(Integer.parseInt(eventIndex));
        appl.logBuilder.removeLogEvent(logEvent);
    }
}
