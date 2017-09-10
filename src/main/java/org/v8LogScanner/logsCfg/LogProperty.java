package org.v8LogScanner.logsCfg;

public class LogProperty {

    public enum logProperties {all, sql, plansqltext}

    private String name;

    public LogProperty() { }

    public LogProperty(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {return name;}
}
