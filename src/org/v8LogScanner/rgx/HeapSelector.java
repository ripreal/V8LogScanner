package org.v8LogScanner.rgx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class HeapSelector implements IRgxSelector{
  
  private int cursorIndex = 0;

  private ConcurrentMap<String, List<String>> rgxResult = new ConcurrentHashMap<>();
  
  public void setResult(ConcurrentMap<String, List<String>> _rgxResult){
    rgxResult = _rgxResult;
  }
  
  public ConcurrentMap<String, List<String>> getResult(){
    return rgxResult;
  }
  
  public List<SelectorEntry> select(int count, boolean forward){
    
    if (forward && cursorIndex == rgxResult.size() - 1)
      cursorIndex = 0;
    
    if(!forward && cursorIndex == 0)
      cursorIndex = Math.max(rgxResult.size() - count, 0);
    else if(!forward){
      count = Math.min(cursorIndex, count);
      cursorIndex = cursorIndex - count;
    }
    
    List<SelectorEntry> selected = new ArrayList<>();
    
    rgxResult.
    keySet().
    stream().
    sorted((n1, n2) -> {
      return -Integer.compare(rgxResult.get(n1).size(), rgxResult.get(n2).size());
    }).
    skip(cursorIndex).
    limit(count).forEachOrdered(entryKey-> {
      SelectorEntry row = new SelectorEntry(entryKey, rgxResult.get(entryKey));
      selected.add(row);
    });
    
    if (forward)
      cursorIndex = Math.min(cursorIndex + selected.size(), rgxResult.size() - 1);
    
    if (cursorIndex < 0)
      cursorIndex = 0;
    
    return selected;
  }
  
  public int cursorIndex(){
    return cursorIndex;
  }

  public void clearResult() {
    rgxResult.clear();
    cursorIndex = 0;
  }
}
