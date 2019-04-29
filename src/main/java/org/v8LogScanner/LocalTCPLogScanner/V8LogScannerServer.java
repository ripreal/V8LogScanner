package org.v8LogScanner.LocalTCPLogScanner;

import org.v8LogScanner.LocalTCPConnection.SocketEvent;
import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.LocalTCPConnection.TCPConnection;
import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerData.ScannerCommands;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;

import java.io.*;
import java.net.ServerSocket;
import java.util.*;

public class V8LogScannerServer implements SocketEvent {

    private ServerSocket serverSocket = null;
    private List<TCPConnection> connections = new ArrayList<>();
    private PrintStream infoStream = null;
    private BufferedReader in = null;
    private int port = Constants.serverPort;
    private final V8LogScannerClient localClient;

    public V8LogScannerServer(int port) throws LanServerNotStarted {
        this(port, System.out, System.in);
    }

    public V8LogScannerServer(int port, PrintStream infoStream, InputStream in) throws LanServerNotStarted {

        SocketTemplates socketTemplates = SocketTemplates.instance();

        this.infoStream = infoStream;
        if (in != null)
            this.in = new BufferedReader(new InputStreamReader(in));

        serverSocket = socketTemplates.createServerSocket(port);

        if (serverSocket != null) {
            localClient = new V8LanLogScannerClient();
            logEvent(String.format("Server started on ip:%s(local), %s(wich socket was bound to)",
                    serverSocket.getInetAddress().toString(),
                    serverSocket.getLocalSocketAddress().toString()));
        } else {
            throw new LanServerNotStarted();
        }
    }

    public boolean isRun() {
        return serverSocket != null;
    }

    public void Beginlistenning() {

        if (!isRun())
            return;

        connections.clear();

        Runnable listeningClients = new Runnable() {
            public void run() {
                while (true) {
                    TCPConnection connection = TCPConnection.createTCPConnection(serverSocket);
                    if (connection == null) {
                        logEvent("Listening process interrupted!");
                        break;
                    }
                    connection.addListener(V8LogScannerServer.this);
                    Collection<TCPConnection> conns = Collections.synchronizedList(connections);
                    conns.add(connection);
                    connection.passiveOpen();
                }
            }
        };
        Thread listeningThread = new Thread(listeningClients);
        listeningThread.start();
        logEvent("Connections are listening...Print 'q' to exit.");

        while (in != null) {
            try {
                String userInput = in.readLine();
                if (userInput != null && userInput.compareTo("q") == 0) {
                    stopListening();
                    break;
                } else
                    logEvent("Wrong command. Input 'q' to exit");
            } catch (IOException e) {
                ExcpReporting.LogError(this, e);
            }
        }
    }

    public void stopListening() {

        try {
            serverSocket.close();
            connections.forEach(connection -> connection.close());
        } catch (IOException | ConcurrentModificationException e) {
            // Do nothing. We deliberately interrupt all of the jobs that the server does.
            // I/O errors might be thrown if some clients hold connections with the server
            // ConcurrentModificationException errors might be thrown if the server was
            // busied with closing clients requests inside the connection_Closing() method
        }
    }

    // SUBSCRIPTION TO THE CONNECTIONS AND RGX EVENTS

    @Override
    public void state_Changed(TCPConnection connection, String oldState, String newState) {

        String msg = String.format("Server changed client state from %s to %s",
                oldState, newState);

        logEvent(connection, msg);

    }

    @Override
    public void state_Error(TCPConnection connection, TCPMessages message) {
        String msg = String.format("Connection occured with error %s and was closed",
                message.toString());
        logEvent(connection, msg);
    }

    @Override
    public void connection_Closing(TCPConnection connection) {

        Collection<TCPConnection> conns = Collections.synchronizedCollection(connections);
        synchronized (conns) {
            conns.remove(connection);
        }
    }

    @Override
    public void state_Recieving_Data(TCPConnection connection, Object data) {

        V8LogScannerData dataFromClient = (V8LogScannerData) data;
        if (dataFromClient == null) {
            // It is OK because client does not provide any data to the server.
            // May be it is pinging or it is improper client. Do nothing.
            return;
        }

        logEvent(connection, String.format("Server started client's request %s", dataFromClient.command));

        // HANDLING COMMANDS THAT SENT FROM CLIENT SIDE
        V8LogScannerData answerData = null;

        switch (dataFromClient.command) {
            case GET_LOG_FILES_FROM_CFG:
                answerData = scanLogsInCfgFile(dataFromClient.command);
                break;
            case EXECUTE_RGX_OP:
                startRgxOp(connection, dataFromClient);
                break;
            case SELECT_RGX_RESULT:
                answerData = select(dataFromClient);
                break;
            case GET_RGX_INFO:
                answerData = getFinalInfo(dataFromClient);
                break;
            case GET_CFG_PATHS:
                answerData = getCfgPaths(dataFromClient);
                break;
            case PATH_EXIST:
                answerData = logPathExist(dataFromClient);
                break;
            case RESET_RESULT:
                localClient.resetResult();
                break;
            case RESET_ALL:
                localClient.reset();
                break;
            case READ_CFG_PATHS:
                answerData = readCfgFile(dataFromClient);
                break;
            case WRITE_CFG_FILE:
                answerData = writeCfgFile(dataFromClient);
                break;
            case DELETE_CFG_FILE:
                answerData = deleteCfgFile(dataFromClient);
                break;
            default:
                answerData = new V8LogScannerData(null);
        }

        connection.send(answerData);
        logEvent(connection, String.format("Server finished client's request %s", dataFromClient.command));
    }

    public void invoke(List<String> info) {

    }

    // PERFORMING CLIENT'S REQUEST

    private V8LogScannerData scanLogsInCfgFile(ScannerCommands command) {
        List<String> logs = localClient.scanLogsInCfgFile();
        V8LogScannerData dataToClient = new V8LogScannerData(command);
        dataToClient.putData("logFiles", logs);
        return dataToClient;
    }

    private void startRgxOp(TCPConnection connection, V8LogScannerData dataFromClient) {

        ScanProfile profile = (ScanProfile) dataFromClient.getData("profile");
        localClient.setProfile(profile);
        ProcessEvent procEvent = new ProcessEvent() {
            public void invoke(List<String> info) {
                V8LogScannerData dataToClient = new V8LogScannerData(ScannerCommands.GET_PROC_INFO);
                dataToClient.putData("info", info);
                connection.send(dataToClient);
            }
        };

        localClient.addListener(procEvent);
        localClient.startRgxOp();
    }

    private V8LogScannerData select(V8LogScannerData dataFromClient) {

        int amount = (int) dataFromClient.getData("count");
        SelectDirections direction = (SelectDirections) dataFromClient.getData("direction");
        List<SelectorEntry> serverSelections = localClient.select(amount, direction);

        V8LogScannerData answerData = new V8LogScannerData(dataFromClient.command);
        answerData.putData("selection", serverSelections);
        return answerData;
    }

    private V8LogScannerData getFinalInfo(V8LogScannerData dataFromClient) {

        V8LogScannerData dataToClient = new V8LogScannerData(dataFromClient.command);
        dataToClient.putData("finalInfo", localClient.getFinalInfo());

        return dataToClient;
    }

    private V8LogScannerData getCfgPaths(V8LogScannerData dataFromClient) {

        V8LogScannerData dataToClient = new V8LogScannerData(dataFromClient.command);
        dataToClient.putData("cfgPaths", localClient.getCfgPaths());

        return dataToClient;
    }

    private V8LogScannerData writeCfgFile(V8LogScannerData dataFromClient) {

        V8LogScannerData dataToClient = new V8LogScannerData(dataFromClient.command);

        String content = (String) dataFromClient.getData("content");
        dataToClient.putData("result", localClient.writeCfgFile(content));

        return dataToClient;
    }

    private V8LogScannerData deleteCfgFile(V8LogScannerData dataFromClient) {
        V8LogScannerData dataToClient = new V8LogScannerData(dataFromClient.command);
        dataToClient.putData("result", localClient.deleteCfgFile());
        return dataToClient;
    }

    private V8LogScannerData readCfgFile(V8LogScannerData dataFromClient) {

        List<String> cfgPaths = (List<String>) dataFromClient.getData("cfgPaths");

        V8LogScannerData dataToClient = new V8LogScannerData(dataFromClient.command);
        dataToClient.putData("cfgContent", localClient.readCfgFile(cfgPaths));

        return dataToClient;
    }

    private V8LogScannerData logPathExist(V8LogScannerData dataFromClient) {

        V8LogScannerData dataToClient = new V8LogScannerData(dataFromClient.command);
        String path = (String) dataFromClient.getData("path");
        dataToClient.putData("pathExist", localClient.logPathExist(path));

        return dataToClient;
    }

    // LOGGING

    private void logEvent(TCPConnection connection, String info) {
        Date currDate = new Date();

        String dateText = String.format("%1$tF %2tT, Client %3$s, %4$s",
                currDate,
                currDate.getTime(),
                connection.getIP(),
                info);
        logEvent(dateText);
    }

    private void logEvent(String info) {
        if (infoStream != null)
            infoStream.println(info);
    }

    // EXCEPTIONS

    public class LanServerNotStarted extends Exception {

        private static final long serialVersionUID = -8579129667530273590L;

        public LanServerNotStarted() {
            super(String.format("Server was not started. Another instance is already running or "
                            + "\nIP address %s is incorrect or port %s is closed (used)",
                    SocketTemplates.instance().getLocalHost().getHostAddress(), port));
        }
    }

}
