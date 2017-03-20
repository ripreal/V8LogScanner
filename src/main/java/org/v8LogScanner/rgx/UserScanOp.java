package org.v8LogScanner.rgx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;

public class UserScanOp extends AbstractOp{
  
  private HeapSelector selector = new HeapSelector();
  
  Pattern pattern = null;
  private int totalEvents = 0;
  
//profile variables
  private String rgxExp;
  
  public UserScanOp(ScanProfile profile) {
    this.rgxExp = profile.getRgxExp();
  }
  
  // INTERFACE
  
  public void execute(List<String> logFiles) {
    
    calc.start();
    
    saveProcessingInfo("\n*START OWN RGX LOG SCANNING...");
    
    resetResult();
    
    pattern = Pattern.compile(rgxExp, Pattern.DOTALL);
    
    ConcurrentMap<String, List<String>> rgxResult = new ConcurrentHashMap<>();
    
    for(int i=0; i < logFiles.size(); i++)
    {
      ArrayList<String> mapLogs = new ArrayList<String>();
      try(RgxReader reader = new RgxReader(logFiles.get(i), Constants.logsCharset, Constants.logEventsCount)){
        
        ArrayList<String> readResult;
      
        while (reader.next()){
          readResult = reader.getResult();
          
          inSize += readResult.size();
          mapLogs.addAll(filterLogs(readResult));
          outSize += mapLogs.size();
          totalEvents += mapLogs.size();
        }
        
        if (mapLogs.size() > 0){
          rgxResult.put(logFiles.get(i), mapLogs);
        }
      } catch (IOException e) {
        ExcpReporting.LogError(this.getClass(), e);
      }
      saveProcessingInfo(String.format("in: %s, out: %s, %s", inSize, outSize, logFiles.get(i)));
      inSize = 0;
      outSize = 0;
    }
    selector.setResult(rgxResult);
    calc.end();
  }

  public List<String> filterLogs(ArrayList<String> mapList){
    
    List <String> result = mapList.
      parallelStream().
      unordered().
      filter(n -> pattern.matcher(n).matches()).
      sequential().
      sorted(RgxOpManager::compare).
      collect(Collectors.toList());
    
    return result;
  }

  public void resetResult(){
    selector.clearResult();
    processingInfo.clear();
    inSize = 0;
    outSize = 0;
  }

  public String getFinalInfo(String logDescr) {
    ConcurrentMap<String, List<String>> rgxResult = selector.getResult();
    return String.format(
      "\nSummary:"
      + "\n Total scanned log files: %s"
      + "\n Total log files with matched events: %s"
      + "\n Total matched events: %s"
      + "\n Execution time: %s", logDescr, rgxResult.size(), totalEvents, calc.getTime());
  }

  public List<SelectorEntry> select(int count, SelectDirections direction){
    return selector.select(count, direction);
  }
  
  public int cursorIndex(){
    return selector.cursorIndex();
  }
}
