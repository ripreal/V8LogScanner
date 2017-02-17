package org.v8LogScanner.rgx;

import java.io.Serializable;
import java.util.List;

import org.v8LogScanner.rgx.RegExp.PropTypes;

public interface ScanProfile extends Serializable, Cloneable {
	
	public enum RgxOpTypes {CURSOR_OP, HEAP_OP, USER_OP}
	public enum GroupTypes {BY_PROPS, BY_FILE_NAMES}
	public enum LogTypes   {RPHOST, CLIENT, ANY}
	public enum DateRanges {ANY, LAST_HOUR, TODAY, YESTERDAY, THIS_WEEK, THIS_MONTH, PREV_WEEK, PREV_MONTH, SET_OWN}
	
	int getId();
	
	void setId();
	
	String getName();
	
	void setName(String name);
	
	List<String> getLogPaths();

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

}