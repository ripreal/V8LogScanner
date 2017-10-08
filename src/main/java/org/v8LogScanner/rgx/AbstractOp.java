
package org.v8LogScanner.rgx;
//IMPLEMENTS ELEMENT OF STRATEGY PATTERN
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.v8LogScanner.commonly.Filter.ComparisonTypes;
import org.v8LogScanner.commonly.ProcessListener;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public abstract class AbstractOp extends ProcessListener implements IRgxOp {
  
  // Precompiled variables
  protected ConcurrentMap<RegExp, Pattern> eventPatterns = new ConcurrentHashMap<>();
  protected ConcurrentMap<RegExp, List<String>> groupPropsRgx = new ConcurrentHashMap<>();
  protected ConcurrentMap<RegExp, List<String>> cleanPropsRgx = new ConcurrentHashMap<>();
  protected ConcurrentMap<RegExp, ConcurrentMap<PropTypes, List<String>>> integerFilters = new ConcurrentHashMap<>();
  protected ConcurrentMap<RegExp, ConcurrentMap<PropTypes, ComparisonTypes>> integerCompTypes = new ConcurrentHashMap<>();
  // Service variables
  protected LinkedBlockingQueue<String> processingInfo = new LinkedBlockingQueue<>();
  protected long inSize = 0;
  protected long outSize = 0;
  
  public static IRgxOp buildRgxOp(ScanProfile profile){
    
    RgxOpTypes rgxOp = profile.getRgxOp();
    
    switch (rgxOp) {
    case CURSOR_OP:
      return new CursorOp(profile);
    case HEAP_OP:
      return new HeapOp(profile);
    case USER_OP:
      return new UserScanOp(profile);
    default:
      return new CursorOp(profile);
    }
  }
  
  // INTERFACE
  
  public final ArrayList<String> getProcessingInfo(){
    
    ArrayList<String> result = new ArrayList<>(); 
    processingInfo.forEach(n -> result.add(processingInfo.poll()));
    
    return result;
  }
  
  // PRIVATE
  
  protected final void precompile(List<RegExp> rgxList){
    
    cleanPropsRgx.clear();
    groupPropsRgx.clear();
    integerCompTypes.clear();
    integerFilters.clear();
    eventPatterns.clear();
    groupPropsRgx.clear();
    
    for (RegExp rgx : rgxList){
        
      List<PropTypes> propTypes = rgx.getGroupingProps();
      
      if (propTypes.size() > 0){
      
        List<String> compiledProps = new ArrayList<String>();
        propTypes.forEach(prop -> compiledProps.add(rgx.getThisPropText(prop)));
        if (compiledProps.size() > 0)
          groupPropsRgx.put(rgx, compiledProps);
        
        List<String> compiledCleanProps = new ArrayList<String>();
        propTypes.forEach(prop -> compiledCleanProps.addAll(rgx.getPredecessorPropText(prop)));
        if (compiledCleanProps.size() > 0)
          cleanPropsRgx.put(rgx, compiledCleanProps);
      }
      else
        cleanPropsRgx.put(rgx, rgx.getUnicRgxPropText());
    }
      
    for (RegExp rgx : rgxList){
      Pattern pattern = rgx.compileSpan(PropTypes.ANY, PropTypes.ANY);
      integerCompTypes.put(rgx, rgx.getIntegerCompTypes());
      integerFilters.put(rgx, rgx.getIntegerFilters());
      eventPatterns.put(rgx, pattern);
    }
  }

  protected final void saveProcessingInfo(String info){
    processingInfo.offer(info);
    invoke(getProcessingInfo());
  }
  
}
