package org.v8LogScanner.LocalTCPConnection;

import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;

public interface SocketEvent {
  
  public void state_Changed(TCPConnection connection, String oldState, String newState);
  
  public void state_Error(TCPConnection connection, TCPMessages message);
  
  public void connection_Closing(TCPConnection connection);
  
  public void state_Recieving_Data(TCPConnection connection, Object data);
  
}
