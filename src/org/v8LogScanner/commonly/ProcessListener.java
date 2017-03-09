package org.v8LogScanner.commonly;
//DEFINE OBSERVER PATTERN
import java.util.ArrayList;
import java.util.List;

public class ProcessListener {
  
  private List<ProcessEvent> listeners = new ArrayList<>();
  
  public void addListener(ProcessEvent e){
    listeners.add(e);
  }
  
  public List<ProcessEvent> getListeners(){
    return listeners;
  }
  
  protected void invoke(List<String> info){
    listeners.forEach(n -> n.invoke(info));
  }
}
