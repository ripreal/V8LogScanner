package org.v8LogScanner.testV8LogScanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager.LogScannerClientNotFoundServer;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerServer.LanServerNotStarted;
import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.MenuCmd;
import org.v8LogScanner.cmdAppl.MenuItemCmd;
import org.v8LogScanner.cmdScanner.V8LogScannerAppl;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.testV8LogScanner.V8LogFileConstructor.LogFileTypes;

@RunWith(MockitoJUnitRunner.class)
public class TestCmdAppl {
  
  private String logFileName = "";
  
  @Before
  public void setup() {
    V8LogFileConstructor constructor = new V8LogFileConstructor();
    logFileName = constructor
      .addEXCP()
      .build(LogFileTypes.FILE);
  }
  
  @Test
  public void testExcpReporting() throws LogScannerClientNotFoundServer {
    
    ExcpReporting.out = null;
    
    V8LanLogScannerClient client = new V8LanLogScannerClient();
    try {
      client.cursorIndex();
      fail("should throw ErrorStreamOutNotSet exception");
    }
    catch (ExcpReporting.ErrorStreamOutNotSet e){
      assertTrue(true);
    }
  }
  
  @Test
  public void testRunAppl() throws IOException{
    
    ExcpReporting.out = System.out;
    
    //out
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outStream);
    //in
    ByteBuffer bytes = Charset.forName("UTF-8").encode("1"); // select first menu item
    ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());
    
    ApplConsole appl = new ApplConsole(in, out);
    
    // Test click on menu item
    
    MenuCmd mainMenu = new MenuCmd("main", null);
    mainMenu.add(new MenuItemCmd("first item", null));

    appl.runAppl(mainMenu);
    byte[] outBytes = outStream.toByteArray();
    assertTrue(outBytes.length != 0);
  }
  
  @Test
  public void testRunCmdScannerApp() throws LogScannerClientNotFoundServer, LanServerNotStarted {
    
    ExcpReporting.out = System.out;

    //out
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outStream);
    //in
    StringBuilder sb = new StringBuilder();
    sb.append("1");   //1. Cursor log scanning(recommends)
    sb.append("\n1"); //1. SELECT TOP[100] FROM location[0]
    sb.append("\n3"); //3. Add own log location
    sb.append(String.format("\n%s", logFileName)); // Here we print log location
    sb.append("\n7"); //7. Exit
    sb.append("\n9"); //9. Start
    sb.append("\n");
    ByteBuffer bytes = Charset.forName("UTF-8").encode(sb.toString());
    ByteArrayInputStream in = new ByteArrayInputStream(bytes.array());
    
    ApplConsole appl = new ApplConsole(in, out);
    V8LogScannerAppl scannerAppl = V8LogScannerAppl.instance();
    scannerAppl.setApplConsole(appl);
    
    scannerAppl.runAppl();
    
    V8LogFileConstructor.deleteLogFile(logFileName);
  }

}

