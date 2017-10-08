package org.v8LogScanner.rgx;

import org.v8LogScanner.commonly.Filter;

public class StrokeFilter extends Filter<String>{

  /**
   * 
   */
  private static final long serialVersionUID = 2027201370278979033L;

  @Override
  public String toString() {
    String comp = getComparisonType().toString();
    return comp + "_" + (getElements().contains("") ? "ANY" : String.join(",", getElements()));
  }
}

