package org.v8LogScanner.LocalTCPLogScanner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.ScanProfile;

public class ClientsManager implements Iterable<V8LogScannerClient>{
  
  private V8LogScannerClient localClient;
  private List<V8LogScannerClient> clients = new ArrayList<V8LogScannerClient>();
  
  public ClientsManager(){
    localClient = new V8LanLogScannerClient();
    clients.add(localClient);
  }
  
  public V8LogScannerClient localClient(){
    return localClient;
  }
  
  public V8LogScannerClient addClient(String host, ProcessEvent e) throws LogScannerClientNotFoundServer{
    
    boolean isIP = SocketTemplates.instance().isConformIpv4(host);
    for(V8LogScannerClient client : clients){
      if (isIP && client.getHostIP().compareTo(host) == 0){
        return client;
      }
      else if (!isIP && client.getHostName().contains(host)) {
        return client;
      }
    }
    
    V8LogScannerClient client = new V8LanLogScannerClient(host);
    
    if (!client.pingServer())
      throw new LogScannerClientNotFoundServer();
    clients.add(client);
    
    client.addListener(e);
    
    return client;
    
  }
  
  // method mostly for testing purposes, but can be used anywhere
  public V8LogScannerClient addClient(V8LogScannerClient client) {
    clients.add(client);
    return client;
  }
  
  public List<V8LogScannerClient> getClients(){
    return clients;
  }

  public Iterator<V8LogScannerClient> iterator() {
    return clients.iterator();
  }
  
  public void reset(){
    resetRemoteClients();
    localClient.reset();
  }
  
  public void resetRemoteClients(){
    clients.forEach(client -> client.reset());
    clients.clear();
    clients.add(localClient);
  }
  
  public class LogScannerClientNotFoundServer extends Exception{
    
    private static final long serialVersionUID = 8033069095176280354L;
    
    public LogScannerClientNotFoundServer() {
      super("LAN Server not found!");
    }
  }

  public void startRgxOp() {
    ScanProfile localProfile = localClient.getProfile();
    forEach(client -> {
      // Each client profile has unique log paths so we take them
      ScanProfile cloned = localProfile.clone();
      cloned.setLogPaths(client.getProfile().getLogPaths());
      client.setProfile(cloned);
    });
    forEach(client -> client.startRgxOp());
  }
}
