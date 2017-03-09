package org.HelpTools.trashPrototype;

public class MSQLQueries extends DBqueries {
  /*
  public QueryManager.opStatus setConnection(Properties connProps, boolean autoCommit){
     
    connection = new MSSQLConnection(connProps, true);
    
    return (connection.connEstablish())? QueryManager.opStatus.Success :
      QueryManager.opStatus.error; 
  }  

  public QueryManager.opStatus renameTable(String oldName, String newName){    
    
    if (!connection.connEstablish()){
      ExcpReporting.LogRecord(ExcpReporting.MsgType.NoConn, null);      
      return QueryManager.opStatus.error;    
    }        
    String queryText = "{call sp_rename(?, ?)}";    
    return resultDML(connection.execDML(queryText, oldName, newName),
          ExcpReporting.MsgType.renamingTable_OK, ExcpReporting.MsgType.renamingTable_Err);
  }  
  
  public QueryManager.opStatus deleteRow(String table, String colName, String colValue){    
    
    if (connection == null){
      ExcpReporting.LogRecord(ExcpReporting.MsgType.NoConn, null);      
      return QueryManager.opStatus.error;    
    }      
    String queryText = String.format("DELETE FROM %s WHERE %s = ?", table, colName);    
    return resultDML(connection.execDML(queryText, colValue),
        ExcpReporting.MsgType.delRow_OK, ExcpReporting.MsgType.delRow_err);
  }
  public QueryManager.opStatus deleteRow(String table){    
    
    if (connection == null){
      ExcpReporting.LogRecord(ExcpReporting.MsgType.NoConn, null);      
      return QueryManager.opStatus.error;    
    }      
    String queryText = String.format("DELETE FROM %s", table);
    return resultDML(connection.execDML(queryText),
        ExcpReporting.MsgType.delRow_OK, ExcpReporting.MsgType.delRow_err);
  }
  
  public QueryManager.opStatus MoveAll(String oldTable, String newTable){    
    
    if (connection == null){
      ExcpReporting.LogRecord(ExcpReporting.MsgType.NoConn, null);      
      return QueryManager.opStatus.error;    
    }      
    String queryText = String.format("SELECT * INTO %1$2s FROM %2$2s"+
        "\nDELETE %2$2s",newTable, oldTable);    
    return resultDML(connection.execDML(queryText),
        ExcpReporting.MsgType.move_OK, ExcpReporting.MsgType.move_err);
  }
  
  public QueryManager.opStatus dropTable(String table){
    
    if (connection == null){
      ExcpReporting.LogRecord(ExcpReporting.MsgType.NoConn, null);      
      return QueryManager.opStatus.error;    
    }    
    String queryText = String.format("DROP TABLE %1$2s", table);    
    return resultDML(connection.execDML(queryText),
        ExcpReporting.MsgType.drop_OK, ExcpReporting.MsgType.drop_err);        
  }
  
  public void rollbackResult(){
    if (connection.connEstablish())      
      connection.closeRollback();        
  }
  
  public void commitResult(boolean closeConn){
    if (connection.connEstablish()){
      if (closeConn)
        connection.closeCommit();
      else
        connection.commit();
    }
  }
  
  public QueryManager.opStatus setSavePoint(){
    
    QueryManager.opStatus result = QueryManager.opStatus.error;
    
    if (connection.connEstablish() && connection.setSavepoint())
      result = QueryManager.opStatus.Success;
          
    if (result == QueryManager.opStatus.Success){
      String queryText = "BEGIN TRAN";    
      result = resultDML(connection.execDML(queryText),
          ExcpReporting.MsgType.tran_OK, ExcpReporting.MsgType.tran_err);          
    }                
    return result;    
  }
*/
}
