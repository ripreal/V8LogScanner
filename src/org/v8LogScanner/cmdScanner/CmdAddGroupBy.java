package org.v8LogScanner.cmdScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile.GroupTypes;

public class CmdAddGroupBy implements CmdCommand{
  
  public String getTip() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    if (appl.profile.getGroupType() == GroupTypes.BY_FILE_NAMES)
      return "["+GroupTypes.BY_FILE_NAMES.toString()+"]";
    else if (getSizeGroupingProps(appl.profile.getRgxList()) == 0)
      return "[default props]";
    else{
      List<PropTypes> groups = appl.profile.getRgxList().stream().
      flatMap(event -> event.getGroupingProps().stream()).
      collect(Collectors.toList());
      
      return groups.toString();
    }
  }

  public void execute() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    appl.getConsole().println("");
    
    // asking about group type (by file name or property)
    GroupTypes[] groupTypes = GroupTypes.values();
    String userInput = appl.getConsole().askInputFromList("Choose group type", groupTypes);
    
    if (userInput == null)
      return;
    
    GroupTypes userGroupType = (groupTypes[Integer.parseInt(userInput)]);
    appl.profile.setGroupType(userGroupType);
    if (userGroupType == GroupTypes.BY_FILE_NAMES)
      return;
    
    // asking about event for filter
    userInput = appl.getConsole().askInputFromList("Choose event to group", appl.profile.getRgxList());
    
    if (userInput == null)
      return;
    
  // Asking about prop for filter
    RegExp rgx = appl.profile.getRgxList().get(Integer.parseInt(userInput));
    ArrayList<PropTypes> props = rgx.getPropsForGrouping();
    userInput = appl.getConsole().askInputFromList("Choose property to group", props);
    
    if (userInput == null)
      return;
    
    PropTypes prop = props.get(Integer.parseInt(userInput));
    rgx.getGroupingProps().add(prop);
    
  }

  private long getSizeGroupingProps(List<RegExp> rgxList){
    long userGroupProps = rgxList.stream().
        flatMap(n -> n.getGroupingProps().stream()).
        count();
    return userGroupProps;
  }
  
}
