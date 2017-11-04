package org.v8LogScanner.testV8LogScanner;

import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.commonly.Filter.ComparisonTypes;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.RgxOpManager;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.SelectorEntry;
import org.v8LogScanner.testV8LogScanner.V8LogFileConstructor.LogFileTypes;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRgxOp {

    private V8LogFileConstructor constructor;

    @Before
    public void setup() {
        constructor = new V8LogFileConstructor();
    }

    @Test
    public void testStartRgxOp() throws Exception {

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        String logFileName = constructor
                .addEXCP()
                .build(LogFileTypes.FILE);

        ScanProfile profile = localClient.getProfile();
        profile.addLogPath(logFileName);
        profile.addRegExp(new RegExp(EventTypes.EXCP));
        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(3, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);

    }

    @Test
    public void testfilterEvent() {

        String logFileName = constructor
                .addEXCP()
                .addHASP()
                .build(LogFileTypes.FILE);

        // TEST TIME FILTER
        V8LogScannerClient client = new V8LanLogScannerClient();

        ScanProfile profile = client.getProfile();
        profile.addLogPath(logFileName);

        RegExp excp = new RegExp(EventTypes.EXCP);
        Filter<String> timeFilter = excp.getFilter(PropTypes.Time);
        timeFilter.setComparisonType(ComparisonTypes.equal);
        timeFilter.add("52:17");
        profile.addRegExp(excp);

        client.startRgxOp();
        List<SelectorEntry> logs = client.select(100, SelectDirections.FORWARD);

        assertEquals(1, logs.size());

        // TEST DESCR FILTER
        excp.getFilter(PropTypes.Time).reset();
        excp.getFilter(PropTypes.Descr).add("VResourceSessionImpl.cpp");

        client.startRgxOp();
        logs = client.select(100, SelectDirections.FORWARD);
        assertEquals(1, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);

    }

    @Test
    public void testFilterProcessName() {
        String logFileName = constructor
                .addEXCP()
                .addHASP()
                .addSQlTimeout()
                .build(LogFileTypes.FILE);

        V8LogScannerClient client = new V8LanLogScannerClient();

        ScanProfile profile = client.getProfile();
        profile.addLogPath(logFileName);

        RegExp event = new RegExp(EventTypes.ANY);
        event.getFilter(PropTypes.ProcessName).add("test");
        profile.addRegExp(event);

        client.startRgxOp();
        List<SelectorEntry> logs = client.select(100, SelectDirections.FORWARD);

        assertEquals(1, logs.size());
    }

    @Test
    public void testGroupBySeveralProps() {

        String logFileName = constructor
                .addTDEADLOCK()
                .build(LogFileTypes.FILE);

        V8LogScannerClient client = new V8LanLogScannerClient();

        ScanProfile profile = client.getProfile();
        profile.addLogPath(logFileName);

        RegExp event = new RegExp(EventTypes.TDEADLOCK);
        event.getFilter(PropTypes.Process).add("rphost");
        event.getGroupingProps().add(PropTypes.ClientID);
        event.getGroupingProps().add(PropTypes.ComputerName);
        profile.addRegExp(event);

        client.startRgxOp();
        List<SelectorEntry> logs = client.select(100, SelectDirections.FORWARD);

        assertEquals("t:clientID=56,t:computerName=yardnout", logs.get(0).getKey());
    }

    @Test
    public void testbuildTopSlowestSql() {

        String logFileName = constructor
                .addSDBL()
                .addDBMSSQL()
                .addEXCP()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildTopSlowestSql(profile);
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(2, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildSlowestEventsByUser() {

        String logFileName = constructor
                .addUserEXCP()
                .addDBMSSQL()
                .addSDBL()
                .addEXCP()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildSlowestEventsByUser(profile, "DefUser");
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(3, logs.size());
        assertTrue(logs.get(0).getKey().matches("(?s).*SDBL.*"));

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testbuildTopSlowestSqlByUser() {
        String logFileName = constructor
                .addSDBL()
                .addDBMSSQL()
                .addDBMSSQLUserRIP()
                .addEXCP()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildTopSlowestSqlByUser(profile, "RIP");
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(1, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildSqlLockError() {
        String logFileName = constructor
                .addSQlDEADLOCK()
                .addSQlTimeout()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildSqlDeadlLockError(profile);
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(2, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildRphostExcp() {

        String logFileName = constructor
                .addEXCP()
                .addTDEADLOCK()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildRphostExcp(profile);
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(2, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildFindSQlEventByQueryFragment() {
        String logFileName = constructor
                .addEXCP()
                .addDBMSSQL()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildFindSQlEventByQueryFragment(profile, "FROM Config");
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(1, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildFindSlowSQlEventsWithTableScan() {
        String logFileName = constructor
                .addEXCP()
                .addDBMSSQlWithPlan()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.BuildFindSlowSQlEventsWithPlan(profile,"Table[\\s\\S]+Scan");
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(1, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildUserEXCP() {

        String logFileName = constructor
            .addEXCP()
            .addUserEXCP()
            .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildUserEXCP(profile);
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(1, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuildAllLocksByUser() {

        String logFileName = constructor
                .addEXCP()
                .addTDEADLOCK()
                .addTTimeout()
                .addSQlDEADLOCK()
                .addSQlTimeout()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.buildAllLocksByUser(profile, "DefUser");
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(4, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testBuild1CDeadlocksByConnectID() {

        String logFileName = constructor
                .addTLOCK()
                .addTDEADLOCK()
                //.addTTimeout()
                //.addSQlDEADLOCK()r
                //.addSQlTimeout()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        ScanProfile.build1cDeadlocksByConnectID(profile, "17");
        profile.getRgxList().get(0).getGroupingProps().add(PropTypes.Context);
        profile.addLogPath(logFileName);

        manager.startRgxOp();
        List<SelectorEntry> logs = localClient.select(100, SelectDirections.FORWARD);

        assertEquals(2, logs.size());

        V8LogFileConstructor.deleteLogFile(logFileName);
    }

    @Test
    public void testVSRQuieries() {
        String logFileName = constructor
                .addEXCP()
                .addVRSREQUEST()
                .addVRSRESPONSE()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        profile.addLogPath(logFileName);

        RegExp rgx = new RegExp(EventTypes.VRSREQUEST);
        rgx.getFilter(PropTypes.Method).add("GET");
        profile.addRegExp(rgx);
        profile.addRegExp(new RegExp(EventTypes.VRSRESPONSE));

        localClient.startRgxOp();
        List<SelectorEntry> entries = localClient.select(100, SelectDirections.FORWARD);
        assertEquals(2, entries.size());
    }

    @Test
    public void testQERR() {
        String logFileName = constructor
                .addEXCP()
                .adddQERR()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        profile.addLogPath(logFileName);

        RegExp rgx = new RegExp(EventTypes.QERR);
        profile.addRegExp(rgx);

        localClient.startRgxOp();
        List<SelectorEntry> entries = localClient.select(100, SelectDirections.FORWARD);
        assertEquals(1, entries.size());
    }

    @Test
    public void testUserScanOp() {
        String logFileName = constructor
                .addEXCP()
                .addVRSREQUEST()
                .addVRSRESPONSE()
                .adddQERR()
                .addDBMSSQlWithPlan()
                .build(LogFileTypes.FILE);

        ClientsManager manager = new ClientsManager();
        V8LogScannerClient localClient = manager.localClient();

        ScanProfile profile = localClient.getProfile();
        profile.addLogPath(logFileName);
        profile.setRgxOp(ScanProfile.RgxOpTypes.USER_OP);
        profile.setRgxExp(".*Tab[\\s\\S]+Scan.*");

        localClient.startRgxOp();
        List<SelectorEntry> entries = localClient.select(100, SelectDirections.FORWARD);
        assertEquals(1, entries.size());
    }
}
