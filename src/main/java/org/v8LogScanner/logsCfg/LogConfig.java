package org.v8LogScanner.logsCfg;

import java.util.ArrayList;
import java.util.List;

public class LogConfig {
    public static final String LOG_CFG_NAME = "logcfg.xml";
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
    public static final String DBMSQL = "dbmssql";
    public static final String DBPOSTGRS = "dbpostgrs";
    public static final String DB2 = "db2";
    public static final String DBORACLE = "dboracle";

    public static final String MSQL_TIMEOUT_EN = "Lock request time out period exceeded.";
    public static final String MSQL_TIMEOUT_RU = "Превышено время ожидания запроса на блокировку";
    public static final String MSQL_DEADLOCK_EN = "was deadlocked on lock resources with another process";
    public static final String MSQL_DEADLOCK_RU = "вызвала взаимоблокировку ресурсов";
    public static final String SCRIPT_CIRC_REFS_TAG_NAME = "scriptcircrefs";

    public final static List<String> sql_lock_texts() {
        // MS SQl
        List<String> result = new ArrayList<>();

        result.add(MSQL_TIMEOUT_EN);
        result.add(MSQL_TIMEOUT_RU);
        result.add(MSQL_DEADLOCK_EN);
        result.add(MSQL_DEADLOCK_RU);
        return result;
    }
}
