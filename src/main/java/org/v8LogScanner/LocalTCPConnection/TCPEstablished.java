package org.v8LogScanner.LocalTCPConnection;

import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;

public class TCPEstablished extends TCPState {

    private static TCPEstablished instance;

    private TCPEstablished() {
    }

    public static TCPEstablished instance() {
        if (instance == null)
            instance = new TCPEstablished();
        return instance;
    }

    @Override
    public boolean isEstablished(TCPConnection connection) {
        return !connection.socket.isClosed();
    }

    @Override
    public Object recieve(TCPConnection connection) {
        TCPProtocolMessage response = connection.protocol.getResponse();
        connection.notifyRecievingData(response.data);
        if (response.message == TCPMessages.CONNECTION_GET_ERROR) {
            connection.close();
        }
        return response.data;
    }

    @Override
    public void close(TCPConnection connection) {
        TCPState closedState = TCPClosed.instance();
        closedState.close(connection);
        connection.changeState(closedState);
    }

    @Override
    public boolean send(TCPConnection connection, Object data) {
        return connection.protocol.sendMsg(TCPMessages.SEND_DATA, data);
    }

    @Override
    public void passiveOpen(TCPConnection connection) {
        while (isEstablished(connection)) {
            Object data = recieve(connection);
            // object is sent in  notifyRecievingData
            //send(connection, data);
        }
    }

    @Override
    public void activeOpen(TCPConnection connection) {
        if (isEstablished(connection)) {

        }
    }
}
