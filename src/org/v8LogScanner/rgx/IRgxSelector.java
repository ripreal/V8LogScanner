package org.v8LogScanner.rgx;

import java.util.List;

public interface IRgxSelector {

  public List<SelectorEntry> select(int count, boolean forward);
  
  public int cursorIndex();
  
  public void clearResult();
  
}
