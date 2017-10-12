package org.v8LogScanner.rgx;

import java.io.Serializable;
import java.util.List;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdScanner.V8LogScannerAppl;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.rgx.RegExp.PropTypes;

public interface ScanProfile extends Serializable, Cloneable {

  enum RgxOpTypes {CURSOR_OP, HEAP_OP, USER_OP}
  enum GroupTypes {BY_PROPS, BY_FILE_NAMES}
  enum LogTypes   {RPHOST, CLIENT, RAGENT, RMNGR, ANY}
  enum DateRanges {ANY, THIS_HOUR, LAST_HOUR, TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, PREV_WEEK, PREV_MONTH, SET_OWN}
  
  int getId();
  
  void setId();
  
  String getName();
  
  void setName(String name);
  
  List<String> getLogPaths();
  
  void addLogPath(String logPath);

  void setLogPaths(List<String> logPaths);

  DateRanges getDateRange();

  void setDateRange(DateRanges _dateRange);

  void setUserPeriod(String startDate, String endDate);

  String[] getUserPeriod();

  int getLimit();

  void setLimit(int limit);

  void setRgxOp(RgxOpTypes rgxOp);

  RgxOpTypes getRgxOp();

  LogTypes getLogType();

  void setLogType(LogTypes _logType);

  PropTypes getSortingProp();

  void setSortingProp(PropTypes sortingProp);

  GroupTypes getGroupType();

  void setGroupType(GroupTypes userGroupType);

  List<RegExp> getRgxList();

  void setRgxList(List<RegExp> rgxList);
  
  void addRegExp(RegExp rgx);
  
  String getRgxExp();

  void setRgxExp(String rgx);

  ScanProfile clone();

  void clear();

  ////////////////////
  // MANAGER FUNCTIONS
  ////////////////////

  static void buildTopSlowestSql(ScanProfile profile) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);

    List<RegExp> rgxList = profile.getRgxList();
    RegExp dbmsql = new RegExp(RegExp.EventTypes.DBMSSQL);
    dbmsql.getGroupingProps().add(PropTypes.Context);
    dbmsql.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
    rgxList.add(dbmsql);

    RegExp sdbl = new RegExp(RegExp.EventTypes.SDBL);
    sdbl.getGroupingProps().add(PropTypes.Context);
    sdbl.getFilter(PropTypes.Context).add(""); // only seek events with present prop
    rgxList.add(sdbl);

    RegExp dbv8d8eng = new RegExp(RegExp.EventTypes.DBV8DBEng);
    dbv8d8eng.getGroupingProps().add(PropTypes.Context);
    dbv8d8eng.getFilter(PropTypes.Context).add(""); // only seek events with present prop
    rgxList.add(dbv8d8eng);

    profile.setSortingProp(PropTypes.Duration);
  }

  static void buildTopSlowestSqlByUser(ScanProfile profile, String userName) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);

    List<RegExp> rgxList = profile.getRgxList();
    RegExp dbmsql = new RegExp(RegExp.EventTypes.DBMSSQL);
    dbmsql.getGroupingProps().add(PropTypes.Context);
    dbmsql.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
    dbmsql.getFilter(PropTypes.Usr).add(userName);
    rgxList.add(dbmsql);

    RegExp sdbl = new RegExp(RegExp.EventTypes.SDBL);
    sdbl.getGroupingProps().add(PropTypes.Context);
    sdbl.getFilter(PropTypes.Context).add(""); // only seek events with present prop
    sdbl.getFilter(PropTypes.Usr).add(userName);
    rgxList.add(sdbl);

    RegExp dbv8d8eng = new RegExp(RegExp.EventTypes.DBV8DBEng);
    dbv8d8eng.getGroupingProps().add(PropTypes.Context);
    dbv8d8eng.getFilter(PropTypes.Context).add(""); // only seek events with present prop
    dbv8d8eng.getFilter(PropTypes.Usr).add(userName);
    rgxList.add(dbv8d8eng);

    profile.setSortingProp(PropTypes.Duration);
  }

  static void buildRphostExcp(ScanProfile profile) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.HEAP_OP);

    RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
    excp.getGroupingProps().add(PropTypes.Descr);

    profile.getRgxList().add(excp);
    profile.setLogType(LogTypes.RPHOST);
  }

  static void buildSqlDeadlLockError(ScanProfile profile) {
    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);
    profile.setGroupType(GroupTypes.BY_PROPS);

    List<RegExp> rgxList = profile.getRgxList();

    List<String> sqlTexts = org.v8LogScanner.logsCfg.LogConfig.sql_lock_texts();

    sqlTexts.forEach((sqlMsg) -> {
      RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
      excp.getGroupingProps().add(PropTypes.Descr);
      excp.getFilter(PropTypes.Descr).add(sqlMsg);
      rgxList.add(excp);
    });
  }

  static void build1cDeadlocksError(ScanProfile profile) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);

    List<RegExp> rgxList = profile.getRgxList();

    RegExp v8Timeout = new RegExp(RegExp.EventTypes.TTIMEOUT);
    v8Timeout.getGroupingProps().add(PropTypes.Context);
    v8Timeout.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
    rgxList.add(v8Timeout);

    RegExp v8DeadLock = new RegExp(RegExp.EventTypes.TDEADLOCK);
    v8DeadLock.getGroupingProps().add(PropTypes.Context);
    v8DeadLock.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
    rgxList.add(v8DeadLock);

  }

  static void buildAllLocksByUser(ScanProfile profile) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);

    List<RegExp> rgxList = profile.getRgxList();

    RegExp v8Timeout = new RegExp(RegExp.EventTypes.TTIMEOUT);
    v8Timeout.getGroupingProps().add(PropTypes.Context);
    v8Timeout.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
    rgxList.add(v8Timeout);

    RegExp v8DeadLock = new RegExp(RegExp.EventTypes.TDEADLOCK);
    v8DeadLock.getGroupingProps().add(PropTypes.Context);
    v8DeadLock.getFilter(PropTypes.Context).add(""); // only seek events with existing prop
    rgxList.add(v8DeadLock);

    List<String> sqlTexts = org.v8LogScanner.logsCfg.LogConfig.sql_lock_texts();

    sqlTexts.forEach((sqlMsg) -> {
      RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
      excp.getGroupingProps().add(PropTypes.Descr);
      excp.getFilter(PropTypes.Descr).add(sqlMsg);
      rgxList.add(excp);
    });
  }

  // for investigating locks escalation
  static void buildFindSQlEventByQueryFragment(ScanProfile profile, String query_fragment) {
    profile.clear();
    profile.setRgxOp(RgxOpTypes.HEAP_OP);
    profile.setLogType(LogTypes.RPHOST);
    profile.setGroupType(GroupTypes.BY_PROPS);

    List<RegExp> rgxList = profile.getRgxList();
    RegExp rgx = new RegExp(RegExp.EventTypes.DBMSSQL);
    rgx.getGroupingProps().add(PropTypes.Sql);
    rgx.getFilter(PropTypes.Sql).add(query_fragment);
    rgxList.add(rgx);
  }

  static void BuildFindSlowSQlEventsWithTableScan(ScanProfile profile) {
    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);
    profile.setGroupType(GroupTypes.BY_PROPS);

    List<RegExp> rgxList = profile.getRgxList();
    RegExp rgx = new RegExp(RegExp.EventTypes.DBMSSQL);
    rgx.getGroupingProps().add(PropTypes.Context);
    rgx.getFilter(PropTypes.planSQLText).add("Table Scan");
    rgxList.add(rgx);

    profile.setSortingProp(PropTypes.Duration);
  }

  static void buildUserEXCP(ScanProfile profile) {
    profile.clear();
    profile.setRgxOp(RgxOpTypes.HEAP_OP);

    RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
    excp.getFilter(PropTypes.Usr).add(""); // seek events with present props
    excp.getGroupingProps().add(PropTypes.Usr);
    profile.getRgxList().add(excp);
  }
}
