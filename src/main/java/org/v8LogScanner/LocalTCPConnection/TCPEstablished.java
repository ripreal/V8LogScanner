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
        if (response.message == TCPMessages.CONNECTION_GET_ERROR
            || response.message == TCPMessages.CLOSE_CLIENT) {
            // CLOSING SERVER SIDE
            connection.protocol.sendMsg(TCPMessages.CLOSE_CLIENT);
            SocketTemplates.instance().close(connection.socket);
            if (clientThread != null)
                clientThread.interrupt();
            TCPState closedState = TCPClosed.instance();
            closedState.close(connection);
            connection.changeState(closedState);
        }
        return response.data;
    }

    @Override
    public void close(TCPConnection connection) {
        // CLOSING CLIENT SIDE
        connection.protocol.sendMsg(TCPMessages.CLOSE_CLIENT);
        connection.protocol.getResponse();
        SocketTemplates.instance().close(connection.socket);
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
            recieve(connection);
        }
    }

    @Override
    public void activeOpen(TCPConnection connection) {
        if (isEstablished(connection)) {

        }
    }
}
