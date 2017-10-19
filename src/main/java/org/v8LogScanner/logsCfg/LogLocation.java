package org.v8LogScanner.logsCfg;

import java.io.Serializable;

public class LogLocation implements Serializable {

    private String location = "C:\\v8Logs";
    private String history = "1";

    public LogLocation() {
    }

    public LogLocation(String location, String history) {
        this.location = location;
        this.history = history;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setHistory(int history) {
        this.history = String.valueOf(history);
    }

    public String getHistory() {
        return history;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || (!(obj instanceof LogLocation)))
            return false;
        String other = ((LogLocation) obj).getLocation();
        other = other == null ? "" : other;
        return location.compareTo(other) == 0;
    }
}
