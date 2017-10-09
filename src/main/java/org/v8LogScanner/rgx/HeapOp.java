/////////////////////////////////////
//MAP-REDUCE PATTERN

package org.v8LogScanner.rgx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.rgx.HeapSelector;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.ScanProfile.GroupTypes;

public class HeapOp extends AbstractOp{

  private HeapSelector selector = new HeapSelector();
  private long totalKeys = 0;
  private long mapped = 0;
  private long reduced = 0;
  
//profile variables  
  private List<RegExp> rgxList;
  private GroupTypes groupType;
  
  public HeapOp(ScanProfile profile) {
    this.rgxList = profile.getRgxList();
    this.groupType = profile.getGroupType();
  }
  
  // INTERFACE
  
  public void execute( List<String> logFiles){

    calc.start();
    
    saveProcessingInfo("\n*START HEAP LOG SCANNING...");
    
    resetResult();
    
    precompile(rgxList);

    ConcurrentMap<String, List<String>> rgxResult = new ConcurrentHashMap<>();
    
    for(int i=0; i<logFiles.size(); i++)
    {
      try(RgxReader reader = new RgxReader(logFiles.get(i), Constants.logsCharset, Constants.logEventsCount)){
        
        ArrayList<String> result;
        ConcurrentMap<String, List<String>> mapLogs;
        while (reader.next()){
          
          result = reader.getResult();
          inSize += result.size();
          
          mapLogs = mapLogs(result, logFiles.get(i));
          
          outSize += mapLogs.
              entrySet().
              stream().
              mapToInt( n -> n.getValue().size()).
              sum();
          
          reduceLogs(mapLogs, rgxResult);
          
          mapLogs.clear();
        }
      } catch (IOException e) {
        ExcpReporting.LogError(this.getClass(), e);  
      }
      
      mapped += outSize;
      
      saveProcessingInfo(String.format("in: %s, out: %s, %s", inSize, outSize, logFiles.get(i)));
      inSize = 0;
      outSize = 0;
    }
    
    // later need to estimate effect after applying trimToSize() method to ArrayList instance
    sortLogs(rgxResult);
    
    reduced += rgxResult.
        entrySet().
        stream().
        mapToInt( n -> n.getValue().size()).
        sum();
    
    saveFinalInfo(rgxResult);
    selector.setResult(rgxResult);
    
    calc.end();
  }
  
  public void resetResult(){
    selector.clearResult();
    processingInfo.clear();
    mapped = 0;
    reduced = 0;
    inSize = 0;
    outSize = 0;
  }
  
  public List<SelectorEntry> select(int count, SelectDirections direction) {
    return selector.select(count, direction);
  }
  
  public int cursorIndex(){
    return selector.cursorIndex();
  }
  
  // PRIVATE
  
  private ConcurrentMap<String, List<String>> mapLogs(ArrayList<String> sourceCol, String filename){
    
    ConcurrentMap<String, List<String>> mapResults = null;
    
    if (groupType == GroupTypes.BY_PROPS){
      mapResults = sourceCol.stream().sequential()
        //.parallelStream()
        //.unordered()
        .filter(n -> RgxOpManager.anyMatch(n, eventPatterns, integerFilters, integerCompTypes))
        .collect(Collectors.groupingByConcurrent(input -> {
          return RgxOpManager.getEventProperty(input, eventPatterns, cleanPropsRgx, groupPropsRgx);}
      ));
    }
    else{
      mapResults = sourceCol
        .parallelStream()
        .unordered()
        .filter(n -> RgxOpManager.anyMatch(n, eventPatterns, integerFilters, integerCompTypes))
        .collect(Collectors.groupingByConcurrent(n -> filename));
    }
      
    return mapResults;
  }
  
  private void reduceLogs(ConcurrentMap<String, List<String>> mapLogs, ConcurrentMap<String, List<String>> rgxResult){
    mapLogs.
    entrySet().parallelStream().
    forEach( row -> {
      rgxResult.merge(
        row.getKey(), 
        row.getValue(), 
        (a1,a2) -> {
          a1.addAll(a2); 
          return a1;}
        );
      }
    );
  }
  
  private void sortLogs(ConcurrentMap<String, List<String>> rgxResult){
    
    saveProcessingInfo("\n*SORTING...");
    
    rgxResult.entrySet().
    parallelStream().
    forEach(n -> n.getValue().
      sort(RgxOpManager::compare));
    
    saveProcessingInfo("Sorting completed.");
  }
  
  private void saveFinalInfo(ConcurrentMap<String, List<String>> rgxResult){
    totalKeys = rgxResult.keySet().size();
  }
  
  public String getFinalInfo(String logDescr){
    String results = "";
    if (mapped == reduced)
      results = String.format("\nMap-Reduce has been finished sucessfull! "
              + "Total events reduced: %s (equals mapped)", reduced);
    else
      results = String.format("Map-Reduce has been finished with errors: "
        + "mapped(%s) doesn't equal reduced(%s)!", mapped, reduced);
    
    return results + String.format(
      "\nSummary:"
      + "\n Total log files: %s"
      + "\n Total events: %s"
      + "\n Total keys: %s"
      + "\n Execution time: %s", logDescr, reduced, totalKeys, calc.getTime());
  }
  
}
