package org.HelpTools.trashPrototype;

public interface V83Connection {
/*
  public QueryManager.opStatus connect(String _server, String _base, String _user);
  
  public QueryManager.opStatus connect(String _fileBasePath, String _user);
  
  public QueryManager.opStatus changeUserPassword(String user, String newPassword);
  
  public class V83COM implements V83Connection{
      
      private String server = "";
      private String base = "";
      private String user = "";  
      private ActiveXComponent connCOM = null;
      
      // These methods get access to a 1C instances by COM Automation 32 bit comcntrl.dll library. 
      // Does not work on linux systems.
      
      // server base
      public QueryManager.opStatus connect(String _server, String _base, String _user){

        server = _server;
        base = _base;
        user = _user;
            
        String connString = String.format("Srvr=\"%1$2s\";Ref=\"%2$2s\";", server, base);    
        if (!user.isEmpty())
          connString = String.format(connString + "Usr=\"%1$2s\";", user);
        
        return connect(connString);
  
      }    
      // file base
      public QueryManager.opStatus connect(String _fileBasePath, String _user){
      
        base = _fileBasePath;
        user = _user;
            
        String connString = String.format("File=\"%1$2s\";", base);    
        if (!user.isEmpty())
          connString = String.format(connString + "Usr=\"%1$2s\";", user);  
        
        return connect(connString);
      }  
      // connect by string
      public QueryManager.opStatus connect(String connString){
        
        QueryManager.opStatus result = QueryManager.opStatus.error;
        
        if (System.getProperty("jacob.dll.path") == null)
          System.setProperty("jacob.dll.path", Constants.libDir + "jacob-1.18-x64.dll");
        
        connCOM = new ActiveXComponent("V83.Application");
        try {  
          connCOM.invoke("Connect", connString);
          connCOM.setProperty("Visible", false);
          result = QueryManager.opStatus.Success; 
        }
        catch (Exception e){
          ExcpReporting.LogError(this.getClass(), e);
          result = QueryManager.opStatus.error;
        }
        return result;
      }
      
      public QueryManager.opStatus changeUserPassword(String user, String newPassword ){
        
        QueryManager.opStatus result = QueryManager.opStatus.error;
        try {
          Thread.sleep(5000);
          Dispatch users = connCOM.getProperty("InfoBaseUsers").toDispatch();  
          Dispatch userBase = Dispatch.call(users, "FindByName", user).toDispatch();
          Dispatch.put(userBase, "Password",  newPassword);
          Dispatch.call(userBase, "Write");
          result = QueryManager.opStatus.Success;
        } 
        catch (Exception e) {
          ExcpReporting.LogError(this.getClass(), e);
          result = QueryManager.opStatus.error;
        }    
        return result;
      }  
    }

  public class V83EXE implements V83Connection {
    public String user;
    private String exeLoc = "";  
    private String server = "";
    private String base = "";
    private final String exchName = "_exch.json";
    
    // These methods call 1C instances through *.exe commands.
    // Synchronization is organized with json files in the lib directory.
    
    public QueryManager.opStatus connect(String _server, String _base, String _user){
      String fileName = "C:\\Program Files (x86)\\1cv8\\common\\1cestart.exe";
      boolean inputCorrect;
      do{    
        inputCorrect = Files.isExecutable(Paths.get(fileName));      
        if (!inputCorrect){      
          System.out.println("input 1c.exe location (include file name):");
          fileName = System.console().readLine();
        }      
      }while (!inputCorrect);  
  
      exeLoc = fileName;
      server = _server;
      base = _base;
      
      clearCmdFile();
      return  Run1CWithCommand();
    }
    
    public QueryManager.opStatus connect(String _fileBasePath, String _user){
  
      user = _user;
      
      return null;
    }
    
    private QueryManager.opStatus Run1CWithCommand(){
  
      ProcessBuilder pb = new ProcessBuilder();
      String cmdPathBase = String.format("/S\"%s\\%s\"", server, base);
      String cmdPathEx = String.format("/Execute\"%s\"", Constants.libDir + "commandExecutor.epf");    
      pb.command(exeLoc, cmdPathBase, cmdPathEx);
              
      try {
        pb.start();
      } catch (IOException e) {
        ExcpReporting.LogError(this.getClass(), e);  
        return QueryManager.opStatus.error;
      }  
      
      return   sync1C();  
    }
    
    public QueryManager.opStatus sync1C(){    
      
      int sleepSpan = 1000;
      boolean complete = false;
      int tryCount = 0;
      do {    
        try {
          Thread.sleep(sleepSpan);
        } catch (InterruptedException e) {
          ExcpReporting.LogError(this.getClass(), e);
          break;
        }
        
        complete = recieveCmd();
        
        sleepSpan = (sleepSpan < 5000) ? sleepSpan+=1000 : sleepSpan;  
        tryCount++;
        ExcpReporting.LogRecord(ExcpReporting.MsgType.wait, base);
      } while(!complete && tryCount<20);    
      
      if (!complete){      
        ExcpReporting.LogRecord(ExcpReporting.MsgType.run1C_err, base);
        return QueryManager.opStatus.error;
      }    
      else{
        ExcpReporting.LogRecord(ExcpReporting.MsgType.run1C_OK, base);
        return QueryManager.opStatus.Success;
      }
    }  
    
    public boolean recieveCmd(){
      
      // This format is used when the 1C sends its file to us.
      //{
      //  "reciever": "QueryManager",
      //  "status": "OK",
      //  "no": 1,
      //  "exec": ""
      //}
  
      String jsText ="";
      try{
        jsText = fsys.readAllFromFile(getExchFileName());
      }
      catch(Exception e){
      // file may absent in the directory.
        //ExcpReporting.LogError(this.getClass(), e);
        return false;
      }    
      
      if (jsText.isEmpty())
        return false;
      
      JSONObject job = new JSONObject(jsText); 
      
       int no = job.getInt("no");
       String reciever = job.getString("reciever"); 
       String execRes = job.getString("exec");
       
       if (!execRes.isEmpty() && reciever.compareTo("QueryManager")>=0)
         ExcpReporting.LogRecord(ExcpReporting.MsgType.ownDescr, execRes);
      
       return (no>0 && reciever.compareTo("QueryManager")>=0);        
    }  
      
    public boolean sendCmd(String script){
      
       boolean result = false;
       
       JSONObject obj = new JSONObject();
       obj.put("reciever", "ENT1");     
       obj.put("status", "OK");
       obj.put("no", 1);
       obj.put("exec", script);
  
       
       StringWriter out = new StringWriter();    
       obj.write(out);
       
       String jsonText = out.toString();
       
      try{
        fsys.WriteInNewFile(jsonText, getExchFileName());
        result = true;
      }
      catch (Exception e){
        ExcpReporting.LogError(this.getClass(), e);      
      }    
      
      return result;    
    }
    
    public void clearCmdFile(){
      
      try{ 
        fsys.DeleteFile(getExchFileName());
        ExcpReporting.LogRecord(ExcpReporting.MsgType.clr_OK, base);
        }
      catch(Exception e){
        ExcpReporting.LogError(this.getClass(), e);
        ExcpReporting.LogRecord(ExcpReporting.MsgType.clr_err, base);
        }      
    }  
    
    public String getExchFileName(){
      return Constants.libDir + base + exchName;    
    }
    
    public QueryManager.opStatus changeUserPassword(String user, String newPassword){
      
      QueryManager.opStatus result = QueryManager.opStatus.error;
      
      String script = String.format(
          "FindUser = InfoBaseUsers.FindByName(\"%s\");"
          + "\nFindUser.Password = \"%s\";"
          + "\nFindUser.Write();", user, newPassword);    
        
      if (sendCmd(script)){
        ExcpReporting.LogRecord(ExcpReporting.MsgType.send1C_OK, base);
        result = sync1C();
      }    
      else
        ExcpReporting.LogRecord(ExcpReporting.MsgType.send1C_err, base);
  
      clearCmdFile();
      
      return result;
    }
  }
*/

}
