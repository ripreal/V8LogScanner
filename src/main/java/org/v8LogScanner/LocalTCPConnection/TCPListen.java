package org.v8LogScanner.LocalTCPConnection;

import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;

public class TCPListen extends TCPState {

    private static TCPListen instance;

    private TCPListen() {
    }

    public static TCPListen instance() {
        if (instance == null)
            instance = new TCPListen();
        return instance;
    }

    @Override
    public boolean send(TCPConnection connection, Object data) {

        boolean isSent = false;
        TCPProtocolMessage response = connection.protocol.getResponse();
        if (response.message == TCPMessages.ACCEPT_CLIENT) {
            isSent = connection.protocol.sendMsg(response.message);
            if (isSent) {
                connection.changeState(TCPEstablished.instance());
                connection.recieve();
            } else {
                connection.changeState(TCPClosed.instance());
                connection.notifyStateError(response.message);
                connection.close();
            }
        } else if (response.message == TCPMessages.CONNECTION_GET_ERROR) {
            connection.notifyStateError(response.message);
            connection.changeState(TCPClosed.instance());
            connection.close();
        } else {
            connection.changeState(TCPClosed.instance());
            connection.close();
        }
        return isSent;
    }

}
