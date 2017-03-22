package org.v8LogScanner.commonly;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public interface Filter<T1> extends Iterable<T1>, Serializable {
  
  public enum ComparisonTypes {equal, greater, less, range};
  
  public ComparisonTypes comparisonType();
  
  public void comparisonType(ComparisonTypes comparisonType);
  
  public Filter<T1> add(T1 val);

  public boolean isActive();

  public void reset();

  public int size();

  public T1 get(int index);

  public Iterator<T1> iterator();

  public List<T1> getElements();

  public void setElements(List<T1> elements);

}