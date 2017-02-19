package test;

import org.junit.Before;
import org.junit.Test;
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
    Thread thread = new Thread(() -> server.Beginlistenning());

  }
  
}
