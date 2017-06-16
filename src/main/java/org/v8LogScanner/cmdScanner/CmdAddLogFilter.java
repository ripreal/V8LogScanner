package org.v8LogScanner.cmdScanner;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Filter.ComparisonTypes;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RgxOpManager;
import org.v8LogScanner.rgx.RegExp.PropTypes;

public class CmdAddLogFilter implements CmdCommand {
  
  @Override
  public String getTip() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    boolean filterActive = appl.profile.getRgxList().stream().anyMatch(n -> n.filterActive());
    return filterActive ? "[on]" : "[ANY]";
  }

  @Override
  public void execute() {
  
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    appl.getConsole().println("");
    
    if (appl.profile.getRgxList().size() == 0){
      appl.getConsole().showModalInfo("\n Log event list is empty! First add the log event.");
      return;
    }

    // asking about event for filter
    String userInput = appl.getConsole().askInputFromList("Choose event to filter", appl.profile.getRgxList());
    
    if (userInput == null)
      return;
    
  // Asking about prop for filter
    RegExp rgx = appl.profile.getRgxList().get(Integer.parseInt(userInput));
    ArrayList<PropTypes> props = rgx.getPropsForFiltering();
    userInput = appl.getConsole().askInputFromList("Choose property to filter", props);
    
    if (userInput == null)
      return;
    
    PropTypes prop = props.get(Integer.parseInt(userInput));
    
    // asking about comparison type for filter
    ComparisonTypes userCompType = ComparisonTypes.equal;
    if (RgxOpManager.isNumericProp(prop)){
      ComparisonTypes[] compTypes = ComparisonTypes.values();
      
      userInput = appl.getConsole().askInputFromList("Choose comparison type", compTypes);
      if (userInput == null)
        return;
      
      userCompType = compTypes[Integer.parseInt(userInput)];
      rgx.getFilter(prop).setComparisonType(userCompType);
    }
    
    if (userCompType == ComparisonTypes.range){
    // Asking about filter value
      appl.getConsole().println("");
      String[] message = new String[1];
      message[0] = "Choose start of filter range (including):";
      userInput = appl.getConsole().askInput(message, n -> !n.isEmpty(), true);
      
      userInput = fetchProp(userInput, prop);
      
      if (userInput == null)
        return;

      rgx.getFilter(prop).add(userInput);
      
      appl.getConsole().println("");
      message[0] = "Choose end of filter range (including):";
      userInput = appl.getConsole().askInput(message, n -> !n.isEmpty(), true);
      
      userInput = fetchProp(userInput, prop);
      
      if (userInput == null)
        return;
      
      rgx.getFilter(prop).add(userInput);
    }
    else{
      // Asking about filter value
      appl.getConsole().println("");
      String[] message = new String[1];
      message[0] = "Input filter value. Notice that filter is case-sentitive:";
      userInput = appl.getConsole().askInput(message, n -> !n.isEmpty(), true);
      
      userInput = fetchProp(userInput, prop);
      
      if (userInput == null)
        return;
      rgx.getFilter(prop).add(userInput);
    }
  }
    
  private String fetchProp (String userInput, PropTypes prop){
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    if (userInput == null)
      return null;
    
    if(prop == PropTypes.Time){
      Matcher timeMatcher = Pattern.compile("\\d{2}:\\d{2}").matcher(userInput);
      if (timeMatcher.find())
        userInput = timeMatcher.group().replace(":", "");
      else{
        appl.getConsole().showModalInfo("incorrect format: need  mm:ss");
        return null;
      }
    }
    return userInput;
  }
}
