package org.v8LogScanner.LocalTCPConnection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPProtocol {

    public enum TCPMessages {
        ACCEPT_CLIENT, NOT_ACCEPT_CLIENT, CONNECTION_SEND_ERROR,
        CONNECTION_GET_ERROR, SEND_DATA
    }

    private byte[] senderIP = null;
    private Socket clientSocket;
    private SocketTemplates socketTemplates = SocketTemplates.instance();
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    public TCPProtocol(byte[] senderIP, Socket clientSocket) {
        this.senderIP = senderIP;
        this.clientSocket = clientSocket;
        out = socketTemplates.getOutDataReader(clientSocket);
        in = socketTemplates.getInputDataReader(clientSocket);
    }

    public TCPProtocolMessage getResponse() {
        Object data = socketTemplates.getData(in);
        if (data != null) {
            TCPProtocolMessage message = (TCPProtocolMessage) data;
            return message;
        } else {
            TCPProtocolMessage message = new TCPProtocolMessage();
            message.message = TCPMessages.CONNECTION_GET_ERROR;
            return message;
        }
    }

    public boolean sendMsg(TCPMessages msgType, Object data) {
        TCPProtocolMessage message = new TCPProtocolMessage();
        message.message = msgType;
        message.senderIP = senderIP;
        message.data = data;
        boolean isSent = socketTemplates.sendData(out, message);
        return isSent;
    }

    public boolean sendMsg(TCPMessages msgType) {
        return sendMsg(msgType, null);
    }
}
