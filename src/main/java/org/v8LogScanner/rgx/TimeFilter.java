package org.v8LogScanner.rgx;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.v8LogScanner.commonly.Filter;

public class TimeFilter implements Filter<String>{
  
  private List<String> elements = new ArrayList<>();
  private static final long serialVersionUID = -5249716837573681233L;
  private ComparisonTypes comparisonType = ComparisonTypes.equal;  
  
  @Override
  public Filter<String> add(String val){
    elements.add(fetchProp(val));
    return this;
  }
  
  @Override
  public  boolean isActive(){
    return elements.size() > 0;
  }
  
  @Override
  public void reset(){
    elements.clear();
  }
  
  @Override
  public int size(){
    return elements.size();
  }
  
  @Override
  public String get(int index){
    return elements.get(index);
  }
  
  @Override
  public Iterator<String> iterator() {
    return elements.iterator();
  }
  
  @Override
  public List<String> getElements() {
    return elements;
  }
  
  @Override
  public void setElements(List<String> elements) {
    this.elements = elements;
  }

  @Override
  public ComparisonTypes getComparisonType() {
    return comparisonType;
  }

  @Override
  public void setComparisonType(ComparisonTypes comparisonType) {
    this.comparisonType = comparisonType;
  }
  
  private String fetchProp (String userInput){
    Matcher timeMatcher = Pattern.compile("\\d{2}:\\d{2}").matcher(userInput);
      if (timeMatcher.find()) {
        userInput = timeMatcher.group().replace(":", "");
      }
    return userInput;
  }
}

