package org.v8LogScanner.testV8LogScanner;

import static org.junit.Assert.*;

import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.event.HyperlinkEvent.EventType;

import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class TestScanProfile {
  
  ScanProfile profile;
  
  @Before
  public void setup() {
    ExcpReporting.out = System.out;
    profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
  }
  
  @Test 
  public void testAddLogPath() {
    profile.addLogPath("t:\testpath");
    profile.addLogPath("t:\testpath");
    assertEquals(profile.getLogPaths().size(), 1);
  }
  
  @Test
  public void testLanSerialization() {
    
    RegExp rgx = new RegExp(EventTypes.EXCP);
    rgx.getFilter(PropTypes.Time).add("23:24");
    rgx.getFilter(PropTypes.Descr).add("test");
    profile.addRegExp(rgx);
    
    SocketTemplates templates = SocketTemplates.instance();
    ServerSocket server = templates.createServerSocket(Constants.serverPort);
    assertNotNull(server);
    String ip = templates.getHostIP(server);
    Socket client = templates.createClientSocket(ip, Constants.serverPort);
    
    boolean sent = templates.sendData(client, profile);
    assertTrue(sent);
  }

}
