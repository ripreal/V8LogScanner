package org.v8LogScanner.LocalTCPLogScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.LocalTCPConnection.TCPConnection;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerData.ScannerCommands;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.ProcessListener;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.logs.LogsOperations;
import org.v8LogScanner.rgx.AbstractOp;
import org.v8LogScanner.rgx.IRgxOp;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.GroupTypes;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;
import org.v8LogScanner.rgx.SelectorEntry;

///////////////////////////
//CLASS DEFINES STRATEGY PATTERN
//////////////////////////

public class V8LanLogScannerClient extends ProcessListener implements V8LogScannerClient  {
  
  private List<String> procInfo = new ArrayList<String>();
  private String hostIP = "127.0.0.1";
  private String hostName = "localhost";
  private String clientIP = "127.0.0.1";
  private static Thread listeningHosts;
  
  private ScanProfile profile;
  private IRgxOp rgxOp;
  
  /**
   * 
   * @param host - host IP or host name without domain name
   */
  public V8LanLogScannerClient(String host){
    
    boolean isIP = SocketTemplates.instance().isConformIpv4(host);
    
    String hostIP = isIP ? host : SocketTemplates.instance().getHostIP(host);
    String hostName = isIP ? SocketTemplates.instance().getHostName(hostIP) : host; 
    
    SocketTemplates.instance().getHostIP(host);
    
    this.hostIP = hostIP;
    this.hostName = hostName;
    this.profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
  }
  
  public V8LanLogScannerClient(){
    this.profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
  }
  
  public ScanProfile getProfile() {
    return profile;
  }
  
  public void setProfile(ScanProfile profile) {
    this.profile = profile; 
  }
  
  public String getHostIP(){
    return hostIP;
  }
  
  public String getHostName(){
    return hostName;
  }
  
  public static void beginGettingPossibleHosts(int port, Consumer<String> getNewHost){
    
    Runnable async = new Runnable(){
      @Override
      public void run() {
        SocketTemplates.instance().getPossibleHosts(port, getNewHost);
      }
    };
    listeningHosts = new Thread(async);
    listeningHosts.start();
  }
  
  public static void stopGettingPossibleHost(){
    listeningHosts.interrupt();
  }
  
  //CLIENTS LOCAL PROXY METHODS 
  //////////////////////////////
  
  // GETTING RGX COMMAND  
  
  public int cursorIndex(){
    
    if (rgxOp == null) {
      RgxOpNotStarted exc = new RgxOpNotStarted();
      ExcpReporting.LogError(this.getClass(), exc);
      return 0;
    }
    
    return rgxOp.cursorIndex();
  }
  
  public GroupTypes[] getAllGroupTypes() {
    return GroupTypes.values();
  }
  
  //CLIENTS LAN PROXY METHODS TRANSFERRED VIA TCP PROTOCOL
  //////////////////////////////
  
  public void reset(){
    
    if (isLocalHost()){
      rgxOp = null;
      profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
      return;
    }
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.RESET_ALL);
    
    send(dataToServer);
    
  }
  
  public void resetResult(){
    
    if (isLocalHost()){
      rgxOp = null;
      return;
    }
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.RESET_RESULT);
    
    send(dataToServer);
    
  }
  
  public boolean pingServer(){
    if (isLocalHost())
      return true;
    
    V8LogScannerData dataFromServer = send(null);
    return dataFromServer != null;
    
  }
  
  public boolean logPathExist(String path){
    
    if (isLocalHost())
      return fsys.pathExist(path);
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.PATH_EXIST);
    dataToServer.putData("path", path);
    
    V8LogScannerData dataFromServer = send(dataToServer);
    
    if (dataFromServer == null)
      return false;
    
    return (boolean) dataFromServer.getData("pathExist");
  }
  
  @SuppressWarnings("unchecked")
  public List<String> scanLogsInCfgFile(){
    
    if (isLocalHost())
      return LogsOperations.scanLogsInCfgFile();
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.GET_LOG_FILES_FROM_CFG);
    
    V8LogScannerData dataFromServer = send(dataToServer);
    
    if (dataFromServer == null)
      return null;
    
    return (List<String>) dataFromServer.getData("logFiles");
    
  }
  
  @SuppressWarnings("unchecked")
  public void startRgxOp(){
    
    if (isLocalHost()){
      LogsOperations logsOp = new LogsOperations(); 
      getListeners().forEach(listener -> logsOp.addListener(listener));
      List<String> logFiles = logsOp.readLogFiles(profile);
      
      rgxOp = AbstractOp.buildRgxOp(profile);
      getListeners().forEach(listener -> rgxOp.addListener(listener));
      rgxOp.execute(logFiles);
      return;
    }
    
    outProcessingInfo("Map reduce operation started execution");
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.EXECUTE_RGX_OP);
    
    dataToServer.putData("profile", profile);
    
    TCPConnection connection = TCPConnection.createTCPConnection(hostIP, Constants.serverPort);
    V8LogScannerData dataFromServer = send(connection, dataToServer);
    
    while(dataFromServer != null && dataFromServer.command == ScannerCommands.GET_PROC_INFO){
      invoke((ArrayList<String>) dataFromServer.getData("info"));
      dataFromServer = receive(connection);
    }
    
    
    if (connection != null)
    connection.close();
    
    outProcessingInfo("Map reduce operation finished execution");
  }
  
  @SuppressWarnings("unchecked")
  public List<SelectorEntry> select(int count, boolean forward){
    
    if (isLocalHost())
      return rgxOp.select(count, forward);
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.SELECT_RGX_RESULT);
    dataToServer.putData("count", count);
    dataToServer.putData("forward", forward);
    
    V8LogScannerData dataFromServer = send(dataToServer);
    
    if (dataFromServer == null)
      return null;
    
    List<SelectorEntry> selected = (List<SelectorEntry>) dataFromServer.getData("selection");
    
    return selected;
  }
  
  public String getFinalInfo(){
    
    if (isLocalHost()){
      return rgxOp.getFinalInfo(LogsOperations.getTotalSizeDescr());
    }
    
    V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.GET_RGX_INFO);
    
    V8LogScannerData dataFromServer = send(dataToServer);
    
    if (dataFromServer == null)
      return null;
    
    String info = (String) dataFromServer.getData("finalInfo");
    return info;
  }
  
  // Proxy methods
  
  @Override
  public String toString(){
      return hostIP;
  }
  
  // INNER SECTION
  
  public boolean isLocalHost(){
    return hostIP.compareTo(clientIP) == 0;
  }
  
  private V8LogScannerData send(V8LogScannerData dataToServer){
    TCPConnection connection = TCPConnection.createTCPConnection(hostIP, Constants.serverPort);
    V8LogScannerData dataFromServer = send(connection, dataToServer);
    if (connection != null)
        connection.close();
    return dataFromServer;
  }
  
  private V8LogScannerData send(TCPConnection connection, V8LogScannerData dataToServer){

    if (connection == null){
      if (dataToServer != null){
      // we sent command to execute and get no response from server.
        ExcpReporting.logInfo("Server [" + hostIP + "] not found!");
      }
      return null;
    }
    
    connection.activeOpen();
    
    if (!connection.isEstablished())
      return null;
    
    boolean isSent = connection.send(dataToServer);
    V8LogScannerData dataFromServer = null;
    if (isSent){
      dataFromServer = receive(connection);
    }
    return dataFromServer;
    
  }
  
  private V8LogScannerData receive(TCPConnection connection){
    V8LogScannerData dataFromServer = null;
    try{
      dataFromServer = (V8LogScannerData) connection.recieve();
    }
    catch(Exception e){
      e.printStackTrace();
      return null;
    }
    return dataFromServer;
  }
  
  private void outProcessingInfo(String info){
    String textInfo = String.format("\nHost %s, info: %s ", hostIP, info);
    procInfo.add(textInfo);
    invoke(procInfo);
    procInfo.clear();
  }
  
  public class RgxOpNotStarted extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public RgxOpNotStarted() {
      super("RgxOp has not been executed yet. First you should start executing");
    }
    
  }
  
  
}

