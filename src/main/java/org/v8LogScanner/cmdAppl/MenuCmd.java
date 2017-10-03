/////////////////////////////////////
//COMMAND PATTERN

package org.v8LogScanner.cmdAppl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MenuCmd {
  
  public Supplier<String> name;
  
  private List<MenuItemCmd> items = new ArrayList<>();
  private MenuCmd parent = null;
  private MenuItemCmd back = null;
  
  public MenuCmd(String p_name, MenuCmd parent){
    this.name = () -> p_name;
    this.parent = parent;
    addBack();
  }
  
  public MenuCmd(Supplier<String> p_name, MenuCmd parent){
    this.name = p_name;
    this.parent = parent;
    addBack();
  }
    
  public void add(MenuItemCmd item){
    items.add(item);
  }
  
  public void showMenu(PrintStream out, String title){
    
    out.print(title);
    out.println();
    out.println(name.get());
    out.println();
    for (int i = 1; i <= items.size(); i++){
      String info = String.format("%s. %s", i, items.get(i - 1).getInfo());
      out.println(info);
    }
    // BACK item
    String info = String.format("%s. %s", items.size() + 1, back.getInfo());
    out.println(info);
  }
  
  public MenuCmd clickItem(int index) throws IOException{
    
    MenuItemCmd item = null;
    if (index == items.size())
      item = back;
    else
      item = items.get(index);
    
    MenuCmd menuFromItem = item.Clicked();
    
    if (menuFromItem != null)
      return menuFromItem;
    else if (item.equals(back))
      return clickBack();
    else 
      return this;
  }

  public MenuCmd clickBack() {
    return parent;
  }
  
  public int size(){
    return items.size() + 1;
  }
  
  private void addBack(){
    back = new MenuItemCmd("Exit(q)", null, null);
  }
}
