package org.v8LogScanner.rgx;

import java.io.Serializable;
import java.util.List;

import org.v8LogScanner.rgx.RegExp.PropTypes;

public interface ScanProfile extends Serializable, Cloneable {
  
  public enum RgxOpTypes {CURSOR_OP, HEAP_OP, USER_OP}
  public enum GroupTypes {BY_PROPS, BY_FILE_NAMES}
  public enum LogTypes   {RPHOST, CLIENT, ANY}
  public enum DateRanges {ANY, LAST_HOUR, TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, PREV_WEEK, PREV_MONTH, SET_OWN}
  
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
}
