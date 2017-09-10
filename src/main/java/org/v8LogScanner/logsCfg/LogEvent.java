package org.v8LogScanner.logsCfg;

import org.v8LogScanner.rgx.*;

public class LogEvent {

    public enum LogEventComparisons {eg, like, ne};

    private RegExp.PropTypes prop =  RegExp.PropTypes.Event ;
    private String val =  RegExp.EventTypes.EXCP.toString();
    private LogEventComparisons comparison;

    public LogEvent() {}

    public LogEvent(LogEventComparisons comparison, RegExp.PropTypes prop, String val) {
        this.prop = prop;
        this.comparison = comparison;
        this.val = val;
    }

    public void setComparison(LogEventComparisons comparison) {
        this.comparison = comparison;
    }
    public String getComparison() {
        return comparison.toString();
    }

    public void setProp(RegExp.PropTypes prop) {
        this.prop = prop;
    }
    public String getProp() {
        switch (prop) {
            case Event:
                return "name";
            default:
                return  prop.toString();
        }
    }

    public void setVal(String val) {
        this.val = val;
    }
    public String getVal() {return val;}
}
