package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
<<<<<<< HEAD
import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
=======
>>>>>>> e254fda3cd299408e22ad90ca6b8dd38a89ec55c
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer.LanServerNotStarted;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;

public class TestV8LogScannerServer {
  
  @Before
  public void init() {
    ExcpReporting.out = System.out;
  }
  
  @Test
  public void testBeginListening() throws LanServerNotStarted {
    // start LAN server
    V8LogScannerServer server = new V8LogScannerServer(Constants.serverPort);
<<<<<<< HEAD
    Thread thread = new Thread(() -> server.Beginlistenning());
    thread.start();
    thread.interrupt();
=======
    new Thread(() -> server.Beginlistenning());
    server.stopListening();
    
>>>>>>> e254fda3cd299408e22ad90ca6b8dd38a89ec55c
  }
}
