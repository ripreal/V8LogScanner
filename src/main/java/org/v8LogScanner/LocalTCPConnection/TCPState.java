package org.v8LogScanner.LocalTCPConnection;

public abstract class TCPState {

    protected Thread clientThread = null;

    public Object recieve(TCPConnection connection) {
        return null;
    }

    public void activeOpen(TCPConnection connection) {

    }

    public void passiveOpen(TCPConnection connection) {}

    public void close(TCPConnection connection) {}

    public boolean send(TCPConnection connection, Object data) {
        return false;
    }

    public void synchronize(TCPConnection connection) {}

    /**
     * Must be override for state specific behavior
     *
     * @param connection - current user TCP connection
     * @return boolean value meaning that connection with socket was established
     */
    public boolean isEstablished(TCPConnection connection) {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    protected void changeState(TCPConnection connection, TCPState state) {
        connection.changeState(state);
    }
}
