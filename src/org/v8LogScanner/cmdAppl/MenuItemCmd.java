/////////////////////////////////////
//COMMAND PATTERN

package org.v8LogScanner.cmdAppl;

import java.io.IOException;
import java.util.function.Supplier;

public class MenuItemCmd {
  
  public Supplier<String> name;
  private CmdCommand command = null;
  private MenuCmd menu = null;
  
  public MenuItemCmd(String p_name, CmdCommand _command,  MenuCmd parent){
    menu = parent;
    name = () -> p_name;
    command = _command;
  }
  
  public MenuItemCmd(String p_name, CmdCommand _command){
    name = () -> p_name;
    command = _command;
  }
  
  public MenuItemCmd(Supplier<String> _name, CmdCommand _command){
    name = _name;
    command = _command;
  }
  
  public MenuItemCmd(Supplier<String> _name, CmdCommand _command, MenuCmd parent){
    name = _name;
    menu = parent;
    command = _command;
  }
  
  public String getInfo(){
    return (command == null) ? name.get() : name.get() + command.getTip();
  }
  
  public MenuCmd Clicked() throws IOException{
    if (command != null)
      command.execute();
    return menu;
  }
}
