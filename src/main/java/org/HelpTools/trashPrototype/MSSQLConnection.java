package org.HelpTools.trashPrototype;

public class MSSQLConnection {
/*
  private Connection conn = null;
  private Properties connProps = null;
  private boolean connEstablish = false;
  private Savepoint lastSP = null;
  
  public MSSQLConnection(Properties _connProps, boolean autoCommit){

    connProps = newConnProps();    
    connProps.putAll(_connProps);
    
    String url = String.format("jdbc:sqlserver://%s", connProps.getProperty("server"));
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
    try {
      Driver d = (Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
      DriverManager.registerDriver(d);
      conn = DriverManager.getConnection(url, connProps);
      ExcpReporting.LogRecord(ExcpReporting.MsgType.Connected, getDatabaseName());
      conn.setAutoCommit(autoCommit);
      connEstablish = true;
    } 
    catch (SQLException | InstantiationException | ClassNotFoundException | IllegalAccessException e) {
      ExcpReporting.LogError(this.getClass(), e);    
    }    
  }
  
  public Properties newConnProps(){
    Properties props = new Properties();
    props.setProperty("server","");
    props.setProperty("database","");
    props.setProperty("user","");
    props.setProperty("password","");
    
    return props;    
  }
  
  public String getDatabaseName(){return connProps.getProperty("database");}
  
  public boolean connEstablish(){return connEstablish;}
  
  public void closeRollback()
  {
    try {
      if (lastSP !=null)
        conn.rollback(lastSP);
      else
        conn.rollback(lastSP);
      conn.close();
    } catch (SQLException e) {
      ExcpReporting.LogError(this.getClass(), e);
    }
  }
  
  public void closeCommit()
  {
    try {
      conn.commit();
      conn.close();
    } catch (SQLException e) {
      ExcpReporting.LogError(this.getClass(), e);
    }
  }
  
  public void commit()
  {
    try {
      conn.commit();
    } catch (SQLException e) {
      ExcpReporting.LogError(this.getClass(), e);
    }
  }
  
  public boolean setSavepoint()
  {
    try {
      lastSP = conn.setSavepoint("SavePoint1");      
    } catch (SQLException e) {
      ExcpReporting.LogError(this.getClass(), e);
      return false;
    }
    return true;
  }
  
  public ResultSet execQuery(String textQuery){ 
    
    ResultSet result = null;
            
    try {
      Statement stmt  = conn.createStatement();
      result = stmt.executeQuery(textQuery);
      stmt.close();
    } 
    catch (SQLException e) {
      ExcpReporting.LogError(this.getClass(), e);
    }    
      
    return result;    
  }
  
  public int execDML(String textQuery, String ... pars ) {
     
    int count = -1;
    
    try {      
    
      PreparedStatement pstmt = conn.prepareStatement(textQuery);      
      for (int i = 0; i< pars.length; i++)
        pstmt.setString(i+1, pars[i]);
        
      pstmt.executeUpdate();
      count = pstmt.getUpdateCount();
      pstmt.close();
     }
     catch (Exception e) {
       ExcpReporting.LogError(this.getClass(), e);
       count = -2;
      }
    return count;
  }
*/

}

