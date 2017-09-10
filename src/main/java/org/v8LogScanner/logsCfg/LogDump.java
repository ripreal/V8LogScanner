package org.v8LogScanner.logsCfg;

public class LogDump {

    private boolean create = false;
    private String dumpType = "0";
    private boolean prntscrn = false;

    public void setCreate(boolean create) {
        this.create = create;
    }

    public void setDumpType(String type) {
        this.dumpType = type;
    }

    public void setPrntscrn(boolean prntscrn) {
        this.prntscrn = prntscrn;
    }

    public String getCreate() {
        return String.valueOf(create);
    }
}
