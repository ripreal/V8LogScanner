package org.HelpTools.trashPrototype;

public abstract class DBqueries {
  
  /*
  protected MSSQLConnection connection = null;
  
  public abstract QueryManager.opStatus setConnection(Properties connProps, boolean autoCommit);
  public abstract void commitResult(boolean closeConn);
  public abstract void rollbackResult();
  public abstract QueryManager.opStatus setSavePoint();
  public abstract QueryManager.opStatus renameTable(String oldName, String newName);
  public abstract QueryManager.opStatus deleteRow(String table, String colName, String colValue);
  public abstract QueryManager.opStatus deleteRow(String table);
  public abstract QueryManager.opStatus dropTable(String table);
  public abstract QueryManager.opStatus MoveAll(String oldTable, String newTable);
  
  protected QueryManager.opStatus resultDML(int res_code, 
      ExcpReporting.MsgType msgTypeOK, ExcpReporting.MsgType msgType_err){  
    
    QueryManager.opStatus result = QueryManager.opStatus.error;   
    if (res_code != -2){ 
      ExcpReporting.LogRecord(msgTypeOK,
          connection.getDatabaseName());
      result = QueryManager.opStatus.Success;
    }     
    else{       
      ExcpReporting.LogRecord(msgType_err,
          connection.getDatabaseName());
      result = QueryManager.opStatus.error;
    }
    return result;
  } 
*/
}