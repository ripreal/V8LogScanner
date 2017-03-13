package org.v8LogScanner.LocalTCPLogScanner;

import java.util.List;
import org.v8LogScanner.rgx.SelectorEntry;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.ScanProfile;

public interface V8LogScannerClient {
  
  public String getHostIP();
  
  public String getFinalInfo();
  
  public String getHostName();
  
  public boolean logPathExist(String path);
  
  public void reset();
  
  public void resetResult();
  
  public boolean pingServer();
  
  public List<String> scanLogsInCfgFile();
  
  public void startRgxOp();
  
  public ScanProfile getProfile();
  
  public void setProfile(ScanProfile profile);
  
  public int cursorIndex();
  
  public List<SelectorEntry> select(int count, boolean forward);
  
  public void addListener(ProcessEvent e);
  
}
