package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.ClientsManager.LogScannerClientNotFoundServer;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;

public class CmdAddLogLocServerIP implements CmdCommand {

    @Override
    public String getTip() {
        return V8LogScannerAppl.instance().clientsManager.getClients().toString();
    }

    @Override
    public void execute() {

        V8LogScannerAppl appl = V8LogScannerAppl.instance();

        appl.getConsole().println("Input IP address (something like '127.0.0.1') "
                + "\nor host name(something like 'pc01' without domain name) manually:");

        String userInput = appl.getConsole().askInput(new String[0], n -> {
                    if (n.matches("(\\d|\\.)+"))
                        return n.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
                    else
                        return true; // probably host name
                },
                false);

        if (userInput == null)
            return;

        try {
            appl.clientsManager.addClient(userInput, appl.procEvent);
        } catch (LogScannerClientNotFoundServer e) {
            appl.getConsole().showModalInfo("Server not found!");
        }
    }
}
