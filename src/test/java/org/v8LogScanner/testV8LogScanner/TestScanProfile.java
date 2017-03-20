package org.v8LogScanner.testV8LogScanner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class TestScanProfile {
  
  @Test 
  public void testAddLogPath() {
    ScanProfile profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
    profile.addLogPath("t:\testpath");
    profile.addLogPath("t:\testpath");
    assertEquals(profile.getLogPaths().size(), 1);
  }
}
