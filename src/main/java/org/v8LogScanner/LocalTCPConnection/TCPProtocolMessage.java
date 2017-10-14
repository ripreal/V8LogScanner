package org.v8LogScanner.LocalTCPConnection;

import org.v8LogScanner.LocalTCPConnection.TCPProtocol.TCPMessages;

import java.io.Serializable;

public class TCPProtocolMessage implements Serializable {

    private static final long serialVersionUID = -7544002332861443601L;
    TCPMessages message = null;
    byte[] senderIP = null;
    String info = "";
    Object data = null;

}
