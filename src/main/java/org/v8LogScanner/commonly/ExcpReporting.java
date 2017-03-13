package org.v8LogScanner.commonly;

import java.io.PrintStream;
import java.lang.Exception;
import java.util.Date;

public class ExcpReporting {
  
  public enum MsgType {renamingTable_OK, renamingTable_Err, NoConn, Connected, 
    run1C_OK, run1C_err, send1C_OK, send1C_err, wait, ownDescr, taskCompleted,
    clr_OK, clr_err, delRow_OK, delRow_err, drop_OK, drop_err, move_OK, move_err
    ,tran_OK, tran_err};
  
  public static PrintStream out;
  
  public static void LogError(Object cls, Exception e ){
    
    checkOut();
    
    Date currDate = new Date();
    
    String msg = String.format("ERROR, Time=%1$tF %2tT, Source=%3$2s, Class=%4$2s, Stack=", 
        currDate, currDate.getTime(), e.toString(), cls.toString());
    out.println(msg);
    e.printStackTrace(out);
  }
  
  public static void logInfo(String info){
    
    checkOut();
    
    Date currDate = new Date();
    
    String msg = String.format("INFO, Time=%1$tF %2tT, %3$s", 
        currDate, currDate.getTime(), info);
    out.println(msg);
  }
  
  public static void LogRecord(MsgType msgType, String dbNameOrDescr){
  
    String msg = dbNameOrDescr;
    
    switch(msgType){
    case renamingTable_OK:
      msg = "%s: table renamed successfull.";
      break;
    case renamingTable_Err:
      msg = "%s: table renaming error.";
      break;
    case drop_OK:
      msg = "%s: table drop successfull.";
      break;
    case drop_err:
      msg = "%s: drop table error.";
      break;
    case NoConn:
      msg = "no active connection.";
      break;
    case run1C_OK:
      msg = "%s: sync 1C - message recieved.";
      break;
    case run1C_err:
      msg = "%s: sync 1C - message recieving error.";
      break;
    case Connected:
      msg = "%s: connected.";
      break;
    case send1C_OK:
      msg = "%s: sync 1C - message sent.";
      break;
    case wait:
      msg = "%s: wait...";
      break;
    case send1C_err:
      msg = "%s: sync 1C - message sending error.";
      break;
    case clr_OK:
      msg = "%s: temp files deleted.";
      break;
    case clr_err:
      msg = "%s: error deleting temp files.";
      break;
    case delRow_OK:
      msg = "%s: row deleted successful.";
      break;
    case delRow_err:
      msg = "%s: error deleting row from table.";
      break;
    case ownDescr:
      msg = "%s";
      break;
    case taskCompleted:
      msg = "%s: task completed.";
      break;
    case move_OK:
      msg = "%s: table contest moved successfull.";
      break;
    case move_err:
      msg = "%s: table contest moved with error.";
      break;
    case tran_OK:
      msg = "%s: transaction begins.";
      break;
    case tran_err:
      msg = "%s: transaction begining error.";
      break;
    default:
      msg =  "unknown message.";
    }
    
    msg = String.format(msg, dbNameOrDescr);
    out.println(msg);
  }
  
  private static void checkOut() throws ErrorStreamOutNotSet{
    if (out == null){
     throw new ErrorStreamOutNotSet(); 
    }
  }
  
  public static class ErrorStreamOutNotSet extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public ErrorStreamOutNotSet() {
      super("Global output stream for printing errors is not defined! Set out stream forr error inside the ExcpReporting module");
    }
    
  }
}
