package org.v8LogScanner.testV8LogScanner;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;

public class TestCursorOp {
  
  @Test 
  public void testStartRgxOp() throws Exception {
    
    V8LogScannerClient client = new V8LanLogScannerClient();
    
    Path path = Files.createTempFile("v8LogScanner", "");
    BufferedWriter writer = Files.newBufferedWriter(path);
    writer.write(logFile());
    writer.close();
    
    ScanProfile profile = client.getProfile();
    profile.addLogPath(path.toString());
    profile.addRegExp(new RegExp(EventTypes.EXCP));
    client.startRgxOp();
    List<SelectorEntry> logs = client.select(100, SelectDirections.FORWARD);
    
    assertEquals(3, logs.size());
    
  }
  
  private String logFile() {
    return "52:13.872002-0,EXCP,0,process=1cv8c,setTerminateHandler=setTerminateHandler"
        +"\n52:13.887000-0,EXCP,0,process=1cv8c,setUnhandledExceptionFilter=setUnhandledExceptionFilter"
        +"\n52:17.725002-0,EXCP,2,process=1cv8c,Exception=580392e6-ba49-4280-ac67-fcd6f2180121,Descr='src\\VResourceSessionImpl.cpp(507):";
  }

}
