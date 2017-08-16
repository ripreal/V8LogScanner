package org.v8LogScanner.logsCfg;

public class LogLocation {
    private String location = "";
    private int history = 1;

    public LogLocation(String location, int history) {
        this.location = location;
        this.history = history;
    }

    public void setLocation(String location) { this.location = location; }
    public String getLocation() {return location;}

    public void setHistory(int history) {this.history = history;}
    public int getHistory() {return history;}
}
