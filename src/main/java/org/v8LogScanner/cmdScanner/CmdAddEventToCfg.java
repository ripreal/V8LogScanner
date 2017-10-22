package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.logsCfg.LogEvent;
import org.v8LogScanner.rgx.RegExp;

import java.util.List;
import java.util.Set;

public class CmdAddEventToCfg implements CmdCommand {

    @Override
    public String getTip() {
        return "";
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        // Input prop for existing event or for the new <Event></Event>
        LogEvent[] events = appl.logBuilder.getLogEvents().toArray(new LogEvent[0]);
        LogEvent logEvent = null;
        if (events.length > 0) {
            String[] choices = {"Add new", "Use existing"};
            String choicesRes = appl.getConsole().askInputFromList("Do you want to add new <events> tag or use existing?", choices);
            if (choicesRes == null) {
                return;
            } else if (Integer.parseInt(choicesRes) == 0) {
                logEvent = new LogEvent();
            } else {
                String eventIndex = appl.getConsole().askInputFromList("Input prop:", events);
                if (eventIndex == null)
                    return;
                logEvent = events[Integer.parseInt(eventIndex)];
            }
        } else {
            logEvent = new LogEvent();
        }

        // Input prop <eq></eq>
        RegExp.PropTypes[] allProps = LogEvent.allowedProps().toArray(new RegExp.PropTypes[0]);

        String propTypeIndex = appl.getConsole().askInputFromList(
                "Input prop:",
                allProps
        );

        if (propTypeIndex == null)
            return;

        RegExp.PropTypes propType = allProps[Integer.parseInt(propTypeIndex)];
        //
        String valProp = "";
        if (propType == RegExp.PropTypes.Event) {
            String valPropIndex = appl.getConsole().askInputFromList(
                    "Input value of a prop::",
                    allProps);
            if (valPropIndex == null)
                return;
            valProp = allProps[Integer.parseInt(propTypeIndex)].toString();
        } else {
            String[] textProp = {"input value of a prop:"};
            valProp = appl.getConsole().askInput(textProp, (text) -> !text.isEmpty(), true);
            if (valProp == null)
                return;
        }

        String compTypeIndex = appl.getConsole().askInputFromList(
                "Input comparison type:",
                LogEvent.LogEventComparisons.values()
        );

        if (compTypeIndex == null)
            return;

        logEvent.setProp(propType);
        logEvent.setVal(propType, valProp);
        logEvent.setComparison(propType, LogEvent.LogEventComparisons.values()[Integer.parseInt(compTypeIndex)]);
        appl.logBuilder.addEvent(logEvent);
    }
}
