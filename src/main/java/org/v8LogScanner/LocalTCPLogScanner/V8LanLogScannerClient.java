package org.v8LogScanner.LocalTCPLogScanner;

import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.LocalTCPConnection.TCPConnection;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerData.ScannerCommands;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.ProcessListener;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.logs.LogsOperations;
import org.v8LogScanner.logsCfg.LogBuilder;
import org.v8LogScanner.rgx.AbstractOp;
import org.v8LogScanner.rgx.IRgxOp;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.GroupTypes;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;
import org.v8LogScanner.rgx.SelectorEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

///////////////////////////
//CLASS DEFINES STRATEGY PATTERN
//////////////////////////

public class V8LanLogScannerClient extends ProcessListener implements V8LogScannerClient {

    private List<String> procInfo = new ArrayList<>();
    private String hostIP = "127.0.0.1";
    private String hostName = "localhost";
    private String clientIP = "127.0.0.1";
    private static Thread listeningHosts;
    private Map<String, TCPConnection> connections = new HashMap<>();

    private ScanProfile profile;
    private IRgxOp rgxOp;

    /**
     * @param host - host IP or host name without domain name
     */
    public V8LanLogScannerClient(String host) {

        boolean isIP = SocketTemplates.instance().isConformIpv4(host);

        String hostIP = isIP ? host : SocketTemplates.instance().getHostIP(host);
        String hostName = isIP ? SocketTemplates.instance().getHostName(hostIP) : host;

        SocketTemplates.instance().getHostIP(host);

        this.hostIP = hostIP;
        this.hostName = hostName;
        this.profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
    }

    public V8LanLogScannerClient() {
        this.profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
    }

    public ScanProfile getProfile() {
        return profile;
    }

    public void setProfile(ScanProfile profile) {
        this.profile = profile;
    }

    public String getHostIP() {
        return hostIP;
    }

    public String getHostName() {
        return hostName;
    }

    public static void beginGettingPossibleHosts(int port, Consumer<String> getNewHost) {

        Runnable async = new Runnable() {
            @Override
            public void run() {
                SocketTemplates.instance().getPossibleHosts(port, getNewHost);
            }
        };
        listeningHosts = new Thread(async);
        listeningHosts.start();
    }

    public static void stopGettingPossibleHost() {
        listeningHosts.interrupt();
    }

    //CLIENTS LOCAL PROXY METHODS
    //////////////////////////////

    // GETTING RGX COMMAND

    public int cursorIndex() {

        if (rgxOp == null) {
            RgxOpNotStarted exc = new RgxOpNotStarted();
            ExcpReporting.LogError(this.getClass(), exc);
            return 0;
        }

        return rgxOp.cursorIndex();
    }

    public GroupTypes[] getAllGroupTypes() {
        return GroupTypes.values();
    }

    //CLIENTS LAN PROXY METHODS TRANSFERRED VIA TCP PROTOCOL
    //////////////////////////////

    public void reset() {

        if (isLocalHost()) {
            rgxOp = null;
            profile.clear();
            return;
        }

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.RESET_ALL);

        send(dataToServer);
    }

    public void resetResult() {
        if (isLocalHost()) {
            rgxOp = null;
            return;
        }
        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.RESET_RESULT);
        send(dataToServer);
    }

    public boolean pingServer() {
        if (isLocalHost())
            return true;

        V8LogScannerData dataFromServer = send(null);
        return dataFromServer != null;

    }

    public boolean logPathExist(String path) {

        if (isLocalHost())
            return fsys.pathExist(path);

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.PATH_EXIST);
        dataToServer.putData("path", path);

        V8LogScannerData dataFromServer = send(dataToServer);

        if (dataFromServer == null)
            return false;

        return (boolean) dataFromServer.getData("pathExist");
    }

    @SuppressWarnings("unchecked")
    public List<String> scanLogsInCfgFile() {

        if (isLocalHost())
            return LogsOperations.scanLogsInCfgFile();

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.GET_LOG_FILES_FROM_CFG);

        V8LogScannerData dataFromServer = send(dataToServer);

        if (dataFromServer == null)
            return null;

        return (List<String>) dataFromServer.getData("logFiles");

    }

    @SuppressWarnings("unchecked")
    public void startRgxOp() {

        if (isLocalHost()) {
            LogsOperations logsOp = new LogsOperations();
            getListeners().forEach(listener -> logsOp.addListener(listener));
            List<String> logFiles = logsOp.readLogFiles(profile);

            rgxOp = AbstractOp.buildRgxOp(profile);
            getListeners().forEach(listener -> rgxOp.addListener(listener));
            rgxOp.execute(logFiles);
            return;
        }

        outProcessingInfo("Map reduce operation started execution");

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.EXECUTE_RGX_OP);

        dataToServer.putData("profile", profile);

        V8LogScannerData dataFromServer = send(dataToServer);
        TCPConnection connection = connections.get(hostIP);

        while (dataFromServer != null && dataFromServer.command == ScannerCommands.GET_PROC_INFO) {
            invoke((ArrayList<String>) dataFromServer.getData("info"));
            dataFromServer = receive(connection);
        }

        outProcessingInfo("Map reduce operation finished execution");
    }

    @SuppressWarnings("unchecked")
    public List<SelectorEntry> select(int count, SelectDirections direction) {

        if (isLocalHost())
            return rgxOp.select(count, direction);

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.SELECT_RGX_RESULT);
        dataToServer.putData("count", count);
        dataToServer.putData("direction", direction);

        V8LogScannerData dataFromServer = send(dataToServer);

        if (dataFromServer == null)
            return null;

        List<SelectorEntry> selected = (List<SelectorEntry>) dataFromServer.getData("selection");

        return selected;
    }

    public String getFinalInfo() {

        if (isLocalHost()) {
            return rgxOp.getFinalInfo(LogsOperations.getTotalSizeDescr());
        }

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.GET_RGX_INFO);

        V8LogScannerData dataFromServer = send(dataToServer);

        if (dataFromServer == null)
            return null;

        String info = (String) dataFromServer.getData("finalInfo");
        return info;
    }

    public List<String> getCfgPaths() {

        if (isLocalHost()) {
            LogBuilder builder = new LogBuilder();
            // Path is not serializable so it is transformed before sending to the client
            List<Path> paths = builder.getCfgPaths();
            List<String> resultPaths = new ArrayList<>();
            paths.forEach((item) -> resultPaths.add(item.toString()));
            return resultPaths;
        }

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.GET_CFG_PATHS);
        V8LogScannerData dataFromServer = send(dataToServer);

        if (dataFromServer == null)
            return null;

        List<String> cfgPaths = (List<String>) dataFromServer.getData("cfgPaths");
        return cfgPaths;
    }

    public String readCfgFile(List<String> cfgPaths) {
        if (isLocalHost()) {
            List<Path> paths = new ArrayList<>();
            cfgPaths.forEach((stroke_path) -> paths.add(Paths.get(stroke_path)));
            LogBuilder builder = new LogBuilder(paths);
            return builder.readCfgFile().getContent();
        }

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.READ_CFG_PATHS);
        dataToServer.putData("cfgPaths", cfgPaths);

        V8LogScannerData dataFromServer = send(dataToServer);
        if (dataFromServer == null)
            return null;

        return (String) dataFromServer.getData("cfgContent");
    }

    public String writeCfgFile(String content) {
        if (isLocalHost()) {
            LogBuilder builder = new LogBuilder();
            builder.readCfgFile(content);
            try {
                builder.writeToXmlFile();
            } catch (IOException e) {
                return e.toString();
            }
            return "file saved!";
        }

        V8LogScannerData dataToServer = new V8LogScannerData(ScannerCommands.WRITE_CFG_FILE);
        dataToServer.putData("content", content);

        V8LogScannerData dataFromServer = send(dataToServer);

        if (dataFromServer == null)
            return "file not saved!";

        return (String) dataFromServer.getData("result");
    }

    @Override
    public void close() {
        if (isLocalHost())
            return;

        connections.forEach((ip, conn) -> conn.close());
    }

    // Proxy methods

    @Override
    public String toString() {
        return hostIP;
    }

    // INNER SECTION

    public boolean isLocalHost() {
        return hostIP.compareTo(clientIP) == 0;
    }

    private V8LogScannerData send(V8LogScannerData dataToServer) {

        TCPConnection connection = null;
        if (!connections.entrySet().stream().anyMatch((key) -> key.getKey().matches(hostIP))) {
            connection = TCPConnection.createTCPConnection(hostIP, Constants.serverPort);
            connections.put(hostIP, connection);
        } else {
            connection = connections.get(hostIP);
        }

        V8LogScannerData dataFromServer = send(connection, dataToServer);
        return dataFromServer;
    }

    private V8LogScannerData send(TCPConnection connection, V8LogScannerData dataToServer) {

        if (connection == null) {
            if (dataToServer != null) {
                // we sent command to execute and get no response from server.
                ExcpReporting.logInfo("Server [" + hostIP + "] not found!");
            }
            return null;
        }

        connection.activeOpen();

        if (!connection.isEstablished())
            return null;

        boolean isSent = connection.send(dataToServer);
        V8LogScannerData dataFromServer = null;
        if (isSent) {
            dataFromServer = receive(connection);
        }
        return dataFromServer;

    }

    private V8LogScannerData receive(TCPConnection connection) {
        V8LogScannerData dataFromServer = null;
        try {
            dataFromServer = (V8LogScannerData) connection.recieve();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return dataFromServer;
    }

    private void outProcessingInfo(String info) {
        String textInfo = String.format("\nHost %s, info: %s ", hostIP, info);
        procInfo.add(textInfo);
        invoke(procInfo);
        procInfo.clear();
    }

    public class RgxOpNotStarted extends Exception {

        private static final long serialVersionUID = 1L;

        public RgxOpNotStarted() {
            super("RgxOp has not been executed yet. First you should start executing");
        }

    }
}

