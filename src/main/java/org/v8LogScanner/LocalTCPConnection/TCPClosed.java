package org.v8LogScanner.LocalTCPConnection;

import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;

public class TCPClosed extends TCPState {

    private static TCPClosed instance;

    private TCPClosed() {
    }

    public static TCPClosed instance() {
        if (instance == null)
            instance = new TCPClosed();
        return instance;
    }

    @Override
    public void activeOpen(TCPConnection connection) {

        connection.protocol.sendMsg(TCPMessages.ACCEPT_CLIENT);
        TCPProtocolMessage response = connection.protocol.getResponse();

        if (response.message == TCPMessages.ACCEPT_CLIENT) {
            connection.changeState(TCPEstablished.instance());
        } else if (response.message == TCPMessages.CONNECTION_GET_ERROR) {
            connection.notifyStateError(response.message);
            connection.changeState(TCPClosed.instance());
        } else {
            connection.changeState(TCPClosed.instance());
        }
    }

    @Override
    public void passiveOpen(TCPConnection connection) {

        Runnable clientRunnable = new Runnable() {
            @Override
            public void run() {
                connection.changeState(TCPListen.instance());
                connection.send(null);
            }
        };

        clientThread = new Thread(clientRunnable);
        clientThread.start();
    }

    public void close(TCPConnection connection) {
        connection.notifyConnectionClosing();
    }
}
