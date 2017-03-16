package org.v8LogScanner.testV8LogScanner;

import java.util.List;

import org.junit.Test;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;

public class TestCursorOp {
  
  @Test 
  public void testStartRgxOp() {
    
    V8LogScannerClient client = new V8LanLogScannerClient();

    ScanProfile profile = client.getProfile();
    profile.getLogPaths().add("C:\\v8\\logs");
    profile.addRegExp(new RegExp(EventTypes.EXCP));

    client.startRgxOp();
    List<SelectorEntry> logs = client.select(100, true);

    for (SelectorEntry log : logs) {
      System.out.println(log);
    }
    
  }

}
