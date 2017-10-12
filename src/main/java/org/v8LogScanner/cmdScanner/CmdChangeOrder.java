package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.RegExp.PropTypes;

public class CmdChangeOrder implements CmdCommand {

  @Override
  public String getTip() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    PropTypes prop = appl.profile.getSortingProp();
    if (prop == PropTypes.ANY)
      return "[Events_amount]";
    else
      return "[" + prop + "]";
  }

  @Override
  public void execute() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    String[] sortingProps = new String[2];
    sortingProps[0] = "Events_amount";
    sortingProps[1] = PropTypes.Duration.toString();
    
    String userInput = appl.getConsole().askInputFromList("input prop which will be used to sort result:", sortingProps);
    if (userInput == null){
      return;
    }
    
    String userProp = sortingProps[Integer.parseInt(userInput)];
    if (userProp.compareTo("Events_amount") == 0)
      appl.profile.setSortingProp(PropTypes.ANY);
    else
      appl.profile.setSortingProp(PropTypes.valueOf(userProp));
    
  }
}
