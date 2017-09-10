package org.v8LogScanner.logsCfg;

import org.v8LogScanner.commonly.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogConfig {

    public static final String DUMP_TAG_NAME = "dump";
    public static final String CREATE_NAME = "create";
    public static final String CONFIG_TAG_NAME = "config";
    public static final String CONFIG_PROP_NAME = "xmlns";
    public static final String CONFIG_PROP_VAL = "http://v8.1c.ru/v8/tech-log";
    public static final String PLAN__SQL_TAG_NAME = "plansql";
    public static final String LOC_TAG_NAME = "log";
    public static final String LOC_LOCATION_NAME = "location";
    public static final String LOC_HISTORY_NAME = "history";
    public static final String EVENT_TAG_NAME = "event";
    public static final String PROP_NAME = "property";
    public static final String VAL_NAME = "value";
    public static final String PROPERTY_TAG_NAME = "property";
    public static final String PROPERTY_PROP_NAME = "name";

    public static final List<String> cfgPaths() {
        List<String> result = new ArrayList<>();
        for(String dir : Constants.V8_Dirs()) {
            if (!new File(dir).exists())
                continue;
            result.add(dir + "\\conf\\logcfg.xml");
        }
        return result;
    }
}
