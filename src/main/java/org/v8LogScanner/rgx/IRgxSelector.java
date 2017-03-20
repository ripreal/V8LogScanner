package org.v8LogScanner.rgx;

import java.util.List;

public interface IRgxSelector {
  
  public enum SelectDirections {FORWARD, BACKWARD}
  
  public List<SelectorEntry> select(int count, SelectDirections direction);
  
  public int cursorIndex();
  
  public void clearResult();
  
}
