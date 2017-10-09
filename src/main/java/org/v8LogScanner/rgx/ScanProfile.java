package org.v8LogScanner.rgx;

import java.io.Serializable;
import java.util.List;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdScanner.V8LogScannerAppl;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.rgx.RegExp.PropTypes;

public interface ScanProfile extends Serializable, Cloneable {
  
  public enum RgxOpTypes {CURSOR_OP, HEAP_OP, USER_OP}
  public enum GroupTypes {BY_PROPS, BY_FILE_NAMES}
  public enum LogTypes   {RPHOST, CLIENT, ANY}
  public enum DateRanges {ANY, THIS_HOUR, LAST_HOUR, TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, PREV_WEEK, PREV_MONTH, SET_OWN}
  
  public int getId();
  
  public void setId();
  
  public String getName();
  
  public void setName(String name);
  
  public List<String> getLogPaths();
  
  public void addLogPath(String logPath);

  public void setLogPaths(List<String> logPaths);

  public DateRanges getDateRange();

  public void setDateRange(DateRanges _dateRange);

  public void setUserPeriod(String startDate, String endDate);

  public String[] getUserPeriod();

  public int getLimit();

  public void setLimit(int limit);

  public void setRgxOp(RgxOpTypes rgxOp);

  public RgxOpTypes getRgxOp();

  public LogTypes getLogType();

  public void setLogType(LogTypes _logType);

  public PropTypes getSortingProp();

  public void setSortingProp(PropTypes sortingProp);

  public GroupTypes getGroupType();

  public void setGroupType(GroupTypes userGroupType);

  public List<RegExp> getRgxList();

  public void setRgxList(List<RegExp> rgxList);
  
  public void addRegExp(RegExp rgx);
  
  public String getRgxExp();

  public void setRgxExp(String rgx);

  public ScanProfile clone();

  public void clear();

  ////////////////////
  // MANAGER FUNCTIONS
  ////////////////////

  public static void buildTopSlowestSql(ScanProfile profile) {

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

  public static void buildTopSlowestSqlText(ScanProfile profile) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.CURSOR_OP);
    profile.setLogType(LogTypes.RPHOST);

    List<RegExp> rgxList = profile.getRgxList();

    RegExp dbmsql = new RegExp(RegExp.EventTypes.DBMSSQL);
    dbmsql.getGroupingProps().add(PropTypes.Sql);
    dbmsql.getFilter(PropTypes.Sql).add(""); // only seek events with present prop

    rgxList.add(dbmsql);

    RegExp sdbl = new RegExp(RegExp.EventTypes.SDBL);
    sdbl.getGroupingProps().add(PropTypes.Sdbl);
    sdbl.getFilter(PropTypes.Sdbl).add(""); // only seek events with present prop
    rgxList.add(sdbl);

    RegExp dbv8d8eng = new RegExp(RegExp.EventTypes.DBV8DBEng);
    dbv8d8eng.getGroupingProps().add(PropTypes.Sql);
    dbv8d8eng.getFilter(PropTypes.Sql).add(""); // only seek events with present prop
    rgxList.add(dbv8d8eng);

    profile.setSortingProp(PropTypes.Duration);
  }

  public static void buildRphostExcp(ScanProfile profile) {

    profile.clear();
    profile.setRgxOp(RgxOpTypes.HEAP_OP);

    RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
    excp.getGroupingProps().add(PropTypes.Descr);

    profile.getRgxList().add(excp);
    profile.setLogType(LogTypes.RPHOST);
  }

  public static void buildSqlLockError(ScanProfile profile) {
    profile.clear();
    profile.setRgxOp(RgxOpTypes.HEAP_OP);
    profile.setLogType(LogTypes.RPHOST);
    profile.setGroupType(GroupTypes.BY_PROPS);

    List<RegExp> rgxList = profile.getRgxList();

    List<String> sqlTexts = org.v8LogScanner.commonly.Constants.sql_lock_texts();

    sqlTexts.forEach((sqlMsg) -> {
      RegExp excp = new RegExp(RegExp.EventTypes.EXCP);
      excp.getGroupingProps().add(PropTypes.Descr);
      excp.getFilter(PropTypes.Descr).add(sqlMsg);
      rgxList.add(excp);
    });
  }

  // for investigating locks escalation
  public static void buildFindSQlEventBy(String sql_) {

  }
}
