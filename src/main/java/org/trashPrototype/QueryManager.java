package org.trashPrototype;

public class QueryManager {
  /*
  enum DBProviders {mssql};
  public enum opStatus {Success, error};  
  private ArrayList<DBqueries> queries = new ArrayList<DBqueries>();
  
  public void addQuery(DBProviders provider){
    
    DBqueries query = null;
    switch (provider)  {
      case mssql:        
        query = new MSQLQueries();
        break;
    }
    queries.add(query);
  }  
    
  public void resetPassword(Properties connProps){
    
    opStatus result;  
    for(DBqueries query : queries){  
      
      result = query.setConnection(connProps, true);    
      if (result == opStatus.error)
        continue;
      
      result = query.renameTable("v8users", "tempv8users");  
      if (result == opStatus.error)
        continue;
      
      result = query.deleteRow("dbo.params", "FileName", "users.usr");      
      if (result == opStatus.error){
        query.rollbackResult();
        continue;
      }
      
      V83Connection[] entConn = new V83Connection[2];
      entConn[0] = new V83Connection.V83COM();
      entConn[1] = new V83Connection.V83EXE();

      V83Connection currConn = null;
      for (int i = 0; i< entConn.length; i++)
        if (entConn[i].connect(connProps.getProperty("server1C"), connProps.getProperty("base1C"), "") == opStatus.Success){
          currConn = entConn[i];
          break;
        }
      
      if (currConn == null)
        continue;
      
      result = query.dropTable("v8users");  
      if (result == opStatus.error)
        continue;
      
      result = query.renameTable("tempv8users", "v8users");  
      if (result == opStatus.error)
        continue;
            
      result = currConn.changeUserPassword(connProps.getProperty("user1C"), 
          connProps.getProperty("newPassword1C"));      
      if (result == opStatus.Success){
        query.commitResult(true);
        ExcpReporting.LogRecord(ExcpReporting.MsgType.taskCompleted, 
            connProps.getProperty("base1C"));          
      }
      else
        query.rollbackResult();
    }    
  }  
*/
}
