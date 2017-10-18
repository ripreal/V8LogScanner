package org.v8LogScanner.LocalTCPLogScanner;

import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;

import java.util.List;

public interface V8LogScannerClient {

    String getHostIP();

    String getFinalInfo();

    String getHostName();

    boolean logPathExist(String path);

    void reset();

    void resetResult();

    boolean pingServer();

    List<String> scanLogsInCfgFile();

    void startRgxOp();

    ScanProfile getProfile();

    void setProfile(ScanProfile profile);

    int cursorIndex();

    List<SelectorEntry> select(int count, SelectDirections forward);

    void addListener(ProcessEvent e);

    List<String> getCfgPaths();

    String readCfgFile(List<String> cfgPaths);
}
