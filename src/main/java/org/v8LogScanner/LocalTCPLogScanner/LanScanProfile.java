package org.v8LogScanner.LocalTCPLogScanner;

import java.util.ArrayList;
import java.util.List;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.PropTypes;

public class LanScanProfile implements ScanProfile{

  private static final long serialVersionUID = -2974587904820912760L;
  
  private List<String> sourceLogPaths = new ArrayList<>();
  private DateRanges dateRange        = DateRanges.ANY;
  private int limit                   = 100; // restriction up on amount of events  from
  private LogTypes logType            = LogTypes.ANY;
  private PropTypes sortingProp       = PropTypes.ANY;
  private GroupTypes groupType        = GroupTypes.BY_PROPS;
  private List<RegExp> rgxList        = new ArrayList<RegExp>();
  private String rgxExp               = "";
  private RgxOpTypes rgxOp            = RgxOpTypes.CURSOR_OP;
  private String userStartDate        = "";
  private String userEndDate          = "";
  
  public LanScanProfile(RgxOpTypes rgxOp) {
    this.rgxOp = rgxOp;
  }
  
  // Logs properties
  public List<String> getLogPaths(){ return sourceLogPaths; }
  public void setLogPaths(List<String> logPaths){ sourceLogPaths = logPaths; }
  
  public DateRanges getDateRange() { return dateRange; }
  public void setDateRange(DateRanges _dateRange){ dateRange = _dateRange; }
  
  public void setUserPeriod(String startDate, String endDate){ userStartDate = startDate; userEndDate = endDate; }
  public String[] getUserPeriod(){ return new String[] {userStartDate, userEndDate}; }
  
  public int getLimit(){ return limit; }
  public void setLimit(int limit){ this.limit = limit; }

  // RgxOp properties
  
  public void setRgxOp(RgxOpTypes rgxOp){ this.rgxOp = rgxOp; }
  public RgxOpTypes getRgxOp(){ return rgxOp; }
  
  public LogTypes getLogType() { return logType;}
  public void setLogType(LogTypes _logType) { logType = _logType; }
  
  public PropTypes getSortingProp(){ return sortingProp; }
  public void setSortingProp(PropTypes sortingProp) { this.sortingProp = sortingProp; }

  public GroupTypes getGroupType(){ return groupType; }
  public void setGroupType(GroupTypes userGroupType){ this.groupType = userGroupType; }
  
  public List<RegExp> getRgxList() { return rgxList; }
  public final void setRgxList(List<RegExp> rgxList){ this.rgxList = rgxList; }
  
  public String getRgxExp() { return rgxExp; }
  public void setRgxExp(String rgx) { rgxExp = rgx; }

  public String getName() { return ""; }
  public void setName(String name) { }

  public int getId() { return 0; }
  public void setId() { }
  
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
    cloned.setUserPeriod(userStartDate, userEndDate);
    
    return cloned;
  }


  public void addRegExp(RegExp rgx) {
    if (!rgxList.stream().anyMatch(el -> el.compareTo(rgx) == 0))
      rgxList.add(rgx);
  }
}
