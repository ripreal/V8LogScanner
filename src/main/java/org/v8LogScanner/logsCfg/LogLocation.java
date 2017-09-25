package org.v8LogScanner.logsCfg;

public class LogLocation {

    private String location = "C:\\v8Logs";
    private String history = "1";

    public LogLocation() {}

    public LogLocation(String location, String history) {
        this.location = location;
        this.history = history;
    }

    public void setLocation(String location) { this.location = location; }
    public String getLocation() {
        return location;
    }

    public void setHistory(int history) {this.history = String.valueOf(history);}
    public String getHistory() {return history;}
}
