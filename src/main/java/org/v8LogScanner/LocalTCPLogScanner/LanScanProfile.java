package org.v8LogScanner.LocalTCPLogScanner;

import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile;

import java.util.ArrayList;
import java.util.List;

public class LanScanProfile implements ScanProfile {

    private static final long serialVersionUID = -2974587904820912760L;

    private List<String> sourceLogPaths = new ArrayList<>();
    private DateRanges dateRange;
    private int limit;
    private LogTypes logType;
    private PropTypes sortingProp;
    private GroupTypes groupType;
    private List<RegExp> rgxList;
    private String rgxExp;
    private RgxOpTypes rgxOp;
    private String userStartDate;
    private String userEndDate;
    private String name;
    private int id;

    public LanScanProfile(RgxOpTypes rgxOp) {
        clear();
        this.rgxOp = rgxOp;
    }

    public LanScanProfile() {
        clear();
    }

    // Logs properties
    public List<String> getLogPaths() {
        return sourceLogPaths;
    }

    public void setLogPaths(List<String> logPaths) {
        sourceLogPaths = logPaths;
    }

    public DateRanges getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRanges _dateRange) {
        dateRange = _dateRange;
    }

    public void setStartDate(String startDate) {
        userStartDate = startDate;
    }

    public String getStartDate() {
        return userStartDate;
    }

    public void setEndDate(String startDate) {
        userEndDate = startDate;
    }

    public String getEndDate() {
        return userEndDate;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    // RgxOp properties

    public void setRgxOp(RgxOpTypes rgxOp) {
        this.rgxOp = rgxOp;
    }

    public RgxOpTypes getRgxOp() {
        return rgxOp;
    }

    public LogTypes getLogType() {
        return logType;
    }

    public void setLogType(LogTypes _logType) {
        logType = _logType;
    }

    public PropTypes getSortingProp() {
        return sortingProp;
    }

    public void setSortingProp(PropTypes sortingProp) {
        this.sortingProp = sortingProp;
    }

    public GroupTypes getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupTypes userGroupType) {
        this.groupType = userGroupType;
    }

    public List<RegExp> getRgxList() {
        return rgxList;
    }

    public final void setRgxList(List<RegExp> rgxList) {
        this.rgxList = rgxList;
    }

    public String getRgxExp() {
        return rgxExp;
    }

    public void setRgxExp(String rgx) {
        rgxExp = rgx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {this.id = id;}

    // Others

    public ScanProfile clone() {

        ScanProfile cloned = new LanScanProfile(rgxOp);
        cloned.setDateRange(dateRange);
        cloned.setLimit(limit);
        cloned.setLogType(logType);
        cloned.setSortingProp(sortingProp);
        cloned.setGroupType(groupType);
        cloned.setRgxList(rgxList);
        cloned.setRgxExp(rgxExp);
        cloned.setStartDate(userStartDate);
        cloned.setEndDate(userEndDate);

        return cloned;
    }

    @Override
    public void clear() {
        dateRange = DateRanges.ANY;
        limit = 100; // restriction up amount of events on
        logType = LogTypes.ANY;
        sortingProp = PropTypes.ANY;
        groupType = GroupTypes.BY_PROPS;
        rgxList = new ArrayList<>();
        rgxExp = "";
        rgxOp = RgxOpTypes.CURSOR_OP;
        userStartDate = "";
        userEndDate = "";
        name = "default profile";
    }

    public void addRegExp(RegExp rgx) {
        rgxList.add(rgx);
    }

    public void addLogPath(String logPath) {
        boolean logExist = sourceLogPaths.stream().anyMatch(n -> n.compareTo(logPath) == 0);
        if (!logExist) {
            sourceLogPaths.add(logPath);
        }
    }
}
