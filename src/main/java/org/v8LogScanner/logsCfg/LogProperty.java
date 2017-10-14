package org.v8LogScanner.logsCfg;

public class LogProperty {

    private String name = "all";

    public LogProperty() {

    }

    public LogProperty(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || (!(obj instanceof LogProperty)))
            return false;

        String other = ((LogProperty) obj).getName();
        other = other == null ? "" : other;
        return name.compareTo(other) == 0;
    }
}
