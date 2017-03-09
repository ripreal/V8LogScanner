package org.v8LogScanner.cmdScanner;

import java.util.regex.Pattern;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.logs.LogsOperations;
import org.v8LogScanner.rgx.ScanProfile.DateRanges;

public class CmdChangeLogRange implements CmdCommand {

  @Override
  public String getTip() {
    
    return "[" + LogsOperations.getDatePresentation(V8LogScannerAppl.instance().profile) + "]";
  }

  public void execute() {
    
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    
    DateRanges[] ranges = DateRanges.values();
    String userInput = appl.getConsole().askInputFromList("Choose a range to filter the log files or input your own range:", ranges);
    
    if (userInput == null)
      return;
    
    DateRanges currRange = ranges[Integer.parseInt(userInput)];
    appl.profile.setDateRange(currRange);
    
    if (currRange == DateRanges.SET_OWN){
    
      appl.getConsole().println("");
      String[] message = new String[1];
      message[0] = "Choose start of calendar date (with format - yymmddhh:)";
      
      Pattern pattern = Pattern.compile("^\\d{8}"); 
      String startDate = appl.getConsole().askInput(message, n -> pattern.matcher(n).matches(), true);
      
      message[0] = "Choose end of calendar date (with format - yymmddhh:)";
      String endDate = appl.getConsole().askInput(message, n -> pattern.matcher(n).matches(), true);
      
      appl.profile.setUserPeriod(startDate, endDate);
    }
  }
}
