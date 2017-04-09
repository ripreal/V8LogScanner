package org.v8LogScanner.testV8LogScanner;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.commonly.Filter.ComparisonTypes;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;
import org.v8LogScanner.testV8LogScanner.V8LogFileConstructor.LogFileTypes;

public class TestCursorOp {
  
  private V8LogFileConstructor constructor;
  
  @Before
  public void setup() {
    constructor = new V8LogFileConstructor();
  }
  
  @Test 
  public void testStartRgxOp() throws Exception {
    
    ClientsManager manager = new ClientsManager();
    V8LogScannerClient localClient = manager.localClient(); 
    
    String logFileName = constructor
        .addEXCP()
        .build(LogFileTypes.FILE);
    
    ScanProfile profile = localClient.getProfile();
    profile.addLogPath(logFileName);
    profile.addRegExp(new RegExp(EventTypes.EXCP));
    manager.startRgxOp();
    List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);
    
    assertEquals(3, logs.size());
    
    V8LogFileConstructor.deleteLogFile(logFileName);
    
  }
  
  @Test
  public void testfilterEvent() {
   
    String logFileName = constructor
        .addEXCP()
        .build(LogFileTypes.FILE);
    
    // TEST TIME FILTER
    V8LogScannerClient client = new V8LanLogScannerClient();
    
    ScanProfile profile = client.getProfile();
    profile.addLogPath(logFileName);
    
    RegExp excp = new RegExp(EventTypes.EXCP);
    Filter<String> timeFilter = excp.getFilter(PropTypes.Time);
    timeFilter.comparisonType(ComparisonTypes.equal);
    timeFilter.add("52:17");
    profile.addRegExp(excp);
    
    client.startRgxOp();
    List<SelectorEntry> logs = client.select(100, SelectDirections.FORWARD);
    
    assertEquals(1, logs.size());
    
    // TEST DESCR FILTER
    excp.getFilter(PropTypes.Time).reset();
    excp.getFilter(PropTypes.Descr).add("VResourceSessionImpl.cpp");
    
    client.startRgxOp();
    logs = client.select(100, SelectDirections.FORWARD);
    assertEquals(1, logs.size());
    
    V8LogFileConstructor.deleteLogFile(logFileName);
    
  }
}
