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

    @Override
    public boolean equals(Object obj) {
        if (obj == this)

            return true;
        if (obj == null || (!(obj instanceof LogProperty)))
            return false;
        return (name.compareTo(((LogProperty) obj).getName()) == 0);
    }
}
