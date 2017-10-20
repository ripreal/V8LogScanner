package org.v8LogScanner.logsCfg;

import org.v8LogScanner.rgx.RegExp;

import java.io.Serializable;
import java.util.*;

public class LogEvent implements Iterable<LogEvent.EventRow>, Serializable {
    // eq = equal, ne = not, gt = greater, ge = greather or equal, lt = less, le = less or equal
    public enum LogEventComparisons {
        eq, like, ne, gt, ge, lt, le
    };

    private HashMap<RegExp.PropTypes, EventRow> rows = new HashMap<>();

    private static Map<RegExp.PropTypes, String> tags = new HashMap<>();

    public LogEvent() {
        init();
    }

    private static void init() {
        if (tags.isEmpty()) {
            tags.put(RegExp.PropTypes.Event, "name");
            tags.put(RegExp.PropTypes.Duration, "Durationus");
            tags.put(RegExp.PropTypes.ANY, "");
            tags.put(RegExp.PropTypes.ApplicationName, "t:applicationName");
            tags.put(RegExp.PropTypes.CallID, "Calls");
            tags.put(RegExp.PropTypes.Dbpid, "dbpid");
            tags.put(RegExp.PropTypes.Except, "Exception");
            tags.put(RegExp.PropTypes.ClientID, "t:clientID");
            tags.put(RegExp.PropTypes.ConnectID, "t:connectID");
            tags.put(RegExp.PropTypes.ProcessName, "p:processName");
            tags.put(RegExp.PropTypes.ComputerName, "t:computerName");
            tags.put(RegExp.PropTypes.planSQLText, "plansqltext");
            tags.put(RegExp.PropTypes.Usr, "user");
            tags.put(RegExp.PropTypes.Context, RegExp.PropTypes.Context.toString());
            tags.put(RegExp.PropTypes.Process, RegExp.PropTypes.Process.toString());
            tags.put(RegExp.PropTypes.SessionID, RegExp.PropTypes.SessionID.toString());
            tags.put(RegExp.PropTypes.Descr, RegExp.PropTypes.Descr.toString());
            tags.put(RegExp.PropTypes.Sdbl, RegExp.PropTypes.Sdbl.toString());
            tags.put(RegExp.PropTypes.Trans, RegExp.PropTypes.Trans.toString());
            tags.put(RegExp.PropTypes.WaitConnections, RegExp.PropTypes.WaitConnections.toString());
            tags.put(RegExp.PropTypes.DeadlockConnectionIntersections, RegExp.PropTypes.DeadlockConnectionIntersections.toString());
            tags.put(RegExp.PropTypes.Func, RegExp.PropTypes.Func.toString());
            tags.put(RegExp.PropTypes.Locks, RegExp.PropTypes.Locks.toString());
            tags.put(RegExp.PropTypes.Regions, RegExp.PropTypes.Regions.toString());
            tags.put(RegExp.PropTypes.StackLevel, RegExp.PropTypes.StackLevel.toString());
        }
    }

    // MANAGER METHODS
    public static RegExp.PropTypes parseProp(String prop) throws RuntimeException {
        init();
        try {
            return tags.entrySet()
                    .stream()
                    .filter((item) -> item.getValue().contains(prop))
                    .findFirst()
                    .get()
                    .getKey();
        } catch (NoSuchElementException e) {
            throw new RuntimeException(String.format(
                    "Error. Unknown event property '%s' happen during parsing a log file! It skipped", prop));
        }
    }

    public static Set<RegExp.PropTypes> allowedProps() {
        init();
        return tags.keySet();
    }

    // PUBLIC METHODS
    public LogEvent(LogEventComparisons comparison, RegExp.PropTypes prop, String val) {
        this();
        rows.put(prop, new EventRow(comparison, val));
    }

    public void setProp(RegExp.PropTypes prop) {
        rows.put(prop, new EventRow());
    }

    public void setComparison(RegExp.PropTypes prop, LogEvent.LogEventComparisons comparison) {
        EventRow row = rows.get(prop);
        if (row != null)
            row.setComparison(comparison);
    }

    public void setComparison(RegExp.PropTypes prop, String comparison) {
        LogEventComparisons comp = LogEventComparisons.valueOf(comparison);
        setComparison(prop, comp);
    }

    public void setVal(RegExp.PropTypes prop, String val) {
        EventRow row = rows.get(prop);
        if (row != null)
            row.setVal(val);
    }

    @Override
    public Iterator<LogEvent.EventRow> iterator() {
        //instantiating anonymous class
        return new Iterator<LogEvent.EventRow>() {
            private Iterator<RegExp.PropTypes> keys = rows.keySet().iterator();

            @Override
            public boolean hasNext() {
                return keys.hasNext();
            }

            @Override
            public LogEvent.EventRow next() {
                RegExp.PropTypes key = keys.next();
                LogEvent.EventRow entry = rows.get(key);
                entry.setKey(key);
                return entry;
            }

            @Override
            public void remove() {

            }
        };
    }

    @Override
    public String toString() {
        List<String> entriesLine = new ArrayList<>(rows.size());
        for (EventRow row : this) {
            entriesLine.add(row.toString());
        }
        return String.join("\n", entriesLine);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;
        if (obj == null || (!(obj instanceof LogEvent)))
            return false;

        LogEvent other = ((LogEvent) obj);

        return this.toString().compareTo(other.toString()) == 0;
    }

    public class EventRow {

        private String val = RegExp.EventTypes.EXCP.toString();
        private LogEvent.LogEventComparisons comparison;
        private RegExp.PropTypes key = null;

        public EventRow() {
        }

        public EventRow(LogEvent.LogEventComparisons comparison, String val) {
            this.comparison = comparison;
            this.val = val;
        }

        private void setKey(RegExp.PropTypes key) {
            this.key = key;
        }

        public String getKey() {
            switch (key) {
                case Sql:
                    throw new RuntimeException("sql prop is not supported");
                case Time:
                    throw new RuntimeException("Time prop is not supported");
                case Interface:
                    throw new RuntimeException("Interface prop is not supported");
                case Protected:
                    throw new RuntimeException("Protected prop is not supported");
                default:
                    return LogEvent.tags.getOrDefault(key, key.toString());
            }
        }

        private void setComparison(LogEvent.LogEventComparisons comparison) {
            this.comparison = comparison;
        }

        public String getComparison() {
            return comparison == null ? LogEventComparisons.eq.toString() : comparison.toString();
        }

        private void setVal(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }

        @Override
        public String toString() {
            return String.format("<%s %s=\"%s\" %s=\"%s\"/>",
                    comparison,
                    LogConfig.PROP_NAME, key,
                    LogConfig.VAL_NAME, val);
        }
    }
}
