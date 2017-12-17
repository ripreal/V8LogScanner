package org.v8LogScanner.LocalTCPLogScanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.v8LogScanner.LocalTCPConnection.SocketTemplates;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.rgx.IRgxSelector;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ClientsManager implements Iterable<V8LogScannerClient> {

    private V8LogScannerClient localClient;
    private List<V8LogScannerClient> clients = new ArrayList<>();
    private String profileFileName = "profile.json";

    public ClientsManager() {
        localClient = new V8LanLogScannerClient();
        clients.add(localClient);
    }

    public V8LogScannerClient localClient() {
        return localClient;
    }

    public V8LogScannerClient addClient(String host, ProcessEvent e) throws LogScannerClientNotFoundServer {

        boolean isIP = SocketTemplates.instance().isConformIpv4(host);
        for (V8LogScannerClient client : clients) {
            if (isIP && client.getHostIP().compareTo(host) == 0) {
                return client;
            } else if (!isIP && client.getHostName().contains(host)) {
                return client;
            }
        }

        V8LogScannerClient client = new V8LanLogScannerClient(host);

        if (!client.pingServer())
            throw new LogScannerClientNotFoundServer();
        clients.add(client);

        client.addListener(e);

        return client;

    }

    // method mostly for testing purposes, but can be used anywhere
    public V8LogScannerClient addClient(V8LogScannerClient client) {
        clients.add(client);
        return client;
    }

    public List<V8LogScannerClient> getClients() {
        return clients;
    }

    public Iterator<V8LogScannerClient> iterator() {
        return clients.iterator();
    }

    public void reset() {
        resetRemoteClients();
        localClient.reset();
    }

    public void resetRemoteClients() {
        clients.forEach(V8LogScannerClient::reset);
    }

    public void closeConnections() {
        clients.forEach(V8LogScannerClient::close);
    }

    public void startRgxOp() {
        ScanProfile localProfile = localClient.getProfile();
        forEach(client -> {
            // Each client profile has unique log paths so we take it
            if (client != localClient) {
                ScanProfile cloned = localProfile.clone();
                cloned.getLogPaths().addAll(client.getProfile().getLogPaths());
                client.setProfile(cloned);
            }
        });
        forEach(V8LogScannerClient::startRgxOp);
    }

    public void saveProfile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        ow.writeValue(new File(profileFileName), localClient().getProfile());
    }

    public ScanProfile loadProfile() throws IOException {

        File profileFile = new File(profileFileName);

        if (!profileFile.exists())
            return localClient.getProfile();

        ObjectMapper mapper = new ObjectMapper();
        ScanProfile profile =  mapper.readValue(profileFile, LanScanProfile.class);
        localClient().setProfile(profile);

        return profile;
    }

    public void setProfileFileName(String profileFileName) {this.profileFileName = profileFileName; }

    public void writeResultToFile(V8LogScannerClient userClient, String fileName, int count) {

        List<SelectorEntry> selector = userClient.select(count, IRgxSelector.SelectDirections.FORWARD);

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("PROFILE:");
            writer.write("\n" + userClient.getProfile().getName());
            writer.write("\n");
            writer.write(userClient.getFinalInfo());
            writer.write("\n");
            writer.write(String.format("\nFIRST TOP %s KEYS", count));
            for (int i = 0; i < selector.size(); i++) {
                SelectorEntry entry = selector.get(i);
                writer.write(String.format("\n%s. SIZE: %s,\n%s \n", i, selector.get(i).size(), selector.get(i).getKey()));
                writer.write(String.join("\n", entry.getValue()));
                writer.write("\n");
                writer.write("///////NEXT KEY//////////");
            }
        } catch (IOException e) {
            ExcpReporting.LogError(this, e);
        }
    }

    public class LogScannerClientNotFoundServer extends Exception {

        private static final long serialVersionUID = 8033069095176280354L;

        LogScannerClientNotFoundServer() {
            super("LAN Server not found!");
        }
    }

}
