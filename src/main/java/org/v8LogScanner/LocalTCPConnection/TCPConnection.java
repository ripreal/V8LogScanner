package org.v8LogScanner.LocalTCPConnection;

import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// IMPLEMENTS STATE PATTERN
public class TCPConnection {

    public final Socket socket;

    public final TCPProtocol protocol;
    public final SocketTemplates socketTemplates = SocketTemplates.instance();

    private TCPState state;
    private List<SocketEvent> listeners = new ArrayList<>();
    private String IP_cache = "";

    private TCPConnection(Socket clientSocket) {

        this.socket = clientSocket;
        this.state = TCPClosed.instance();

        byte[] socketIP = socket.getLocalAddress().getAddress();
        IP_cache = socket.getInetAddress().toString();
        this.protocol = new TCPProtocol(socketIP, socket);
    }

    public synchronized static TCPConnection createTCPConnection(ServerSocket socket) {
        Socket clientSocket = SocketTemplates.instance().acceptClient(socket);
         if (clientSocket == null)
            return null;

        return new TCPConnection(clientSocket);
    }

    public synchronized static TCPConnection createTCPConnection(String ip, int port) {

        Socket socket = SocketTemplates.instance().createClientSocket(ip, port);
        if (socket == null)
            return null;

        return new TCPConnection(socket);
    }

    public synchronized void activeOpen() {
        state.activeOpen(this);
    }

    public synchronized void passiveOpen() {
        state.passiveOpen(this);
    }

    public synchronized void close() {
        state.close(this);
    }

    public synchronized boolean send(Object data) {
        return state.send(this, data);
    }

    public void synchronize() {
        state.synchronize(this);
    }

    public Object recieve() {
        return state.recieve(this);
    }

    public synchronized boolean isEstablished() {
        return state.isEstablished(this);
    }

    public String getIP() {
        return IP_cache;
    }

    // NOTIFYING LISTENERS

    public void addListener(SocketEvent listener) {
        listeners.add(listener);
    }

    private void notifyStateChanged(TCPState newState) {
        listeners.forEach(listener ->
                listener.state_Changed(this, this.state.toString(), newState.toString()));
    }

    protected void notifyStateError(TCPMessages error) {
        listeners.forEach(listener -> listener.state_Error(this, error));
    }

    protected void notifyRecievingData(Object data) {
        listeners.forEach(listener -> listener.state_Recieving_Data(this, data));
    }

    protected void notifyConnectionClosing() {
        listeners.forEach(n -> n.connection_Closing(this));
    }

    protected void changeState(TCPState state) {
        notifyStateChanged(state);
        this.state = state;
    }

}
