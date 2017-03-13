package org.v8LogScanner.cmdScanner;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.v8LogScanner.cmdAppl.CmdCommand;

public class CmdAddRgx implements CmdCommand {
  
  @Override
  public String getTip() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    return "[" + appl.profile.getRgxExp() +"]";
  }

  @Override
  public void execute() {
    V8LogScannerAppl appl = V8LogScannerAppl.instance();
    String[] message = {"input regular expression:"};
    String userInput = appl.getConsole().askInput(
      message, 
      n ->{
        try{
          Pattern.compile(n);
        }
        catch(PatternSyntaxException e){
          // incorrect expression
            return false;
          }
        return true;
        }
    ,true);
    
    if (userInput == null){
      return;
    }
    appl.profile.setRgxExp(userInput);
  }
}
