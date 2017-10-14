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

        appl.getConsole().println("Searching possible hosts from LAN... Wait until this process will be ended"
                + "\nor input IP address (something like '127.0.0.1') or host name(something like 'pc01' without domain name) manually:");
        appl.getConsole().println("Available hosts:");
        V8LanLogScannerClient.beginGettingPossibleHosts(Constants.serverPort, host -> {
            appl.getConsole().println(host);
        });

        String userInput = appl.getConsole().askInput(new String[0], n -> {
                    if (n.matches("(\\d|\\.)+"))
                        return n.matches("\\d+\\.\\d+\\.\\d+\\.\\d+");
                    else
                        return true; // probably host name
                },
                false);

        V8LanLogScannerClient.stopGettingPossibleHost();
        if (userInput == null)
            return;

        try {
            appl.clientsManager.addClient(userInput, appl.procEvent);
        } catch (LogScannerClientNotFoundServer e) {
            ExcpReporting.LogError(this, e);
            appl.getConsole().showModalInfo("Server not found!");
        }

    }

}
