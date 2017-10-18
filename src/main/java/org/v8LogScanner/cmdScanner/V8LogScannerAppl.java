package org.v8LogScanner.cmdScanner;

import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.V8LanLogScannerClient;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.MenuCmd;
import org.v8LogScanner.cmdAppl.MenuItemCmd;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.logsCfg.LogBuilder;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//DOMAIN SPECIFIC CONSOLE AND ITS METHODS

public class V8LogScannerAppl {

    public ClientsManager clientsManager;
    public LogBuilder logBuilder;
    public ScanProfile profile;

    private MenuCmd main;
    private static V8LogScannerAppl instance;
    private ApplConsole cmdAppl;

    //subscription to the rgx and logs events
    public final ProcessEvent procEvent = new ProcessEvent() {
        public void invoke(List<String> info) {
            cmdAppl.showInfo(info);
        }
    };

    public static V8LogScannerAppl instance() {

        if (instance == null) {
            instance = new V8LogScannerAppl(new ApplConsole());
        }
        return instance;
    }

    private V8LogScannerAppl(ApplConsole appl) {

        cmdAppl = appl;

        main = new MenuCmd("Main menu:", null);

        clientsManager = new ClientsManager();
        clientsManager.localClient().addListener(procEvent);

        profile = clientsManager.localClient().getProfile();
        profile.addRegExp(new RegExp());
    }

    public void runAppl() {

        MenuCmd cursorLogScan = new MenuCmd("1. Cursor log scanning (recommends)."
                + "\nPerforms orderded Map-Reduce operation and can be used when memory should be consumed carefully or "
                + "\nyou want to use various ordering options. This operation takes user specified TOP amount of sorted events "
                + "\nfrom each log file and performs intermidate reduction through iteration placing results into single array. "
                + "\nAfter that the final reduction takes maximum user specified TOP keys from intermediate reduction array and return them as "
                + "\nresult. Most of the steps are processed sequentially excluding filter and part  of grouping steps that "
                + "\nare processed in parallel. The settings allows you to choose a size of events that have to be taken from " + Constants.logEventsCount
                + "\nevents in every *.log file. Also you can set filtering, grouping and ordering properties."
                + "\n\nMenu:", main);

        MenuCmd heapLogScan = new MenuCmd("2. Heap log scanning."
                + "\nPerfoms a concurrent Map-Reduction operation and can be used when there aren't memory limitations and "
                + "\nyou want to get a complete list of all filtered log events. The mode takes advantage of unsorted "
                + "\nparalell streams and performs mutable mapping and reduction over each step iteration through log files. "
                + "\nMapping and reduction are processed in parallel but the log files themselves are read sequentially."
                + "\nTherefore many small files may reduce the parallel effect. The settings allows you optionally set both "
                + "\nwhich events properties to be filtered out and which properties to be grouped."
                + "\n\nMenu:", main);

        MenuCmd m_userEvents = new MenuCmd("3. Own reg expression log scanning."
                + "\nApplies user defined regular expression (rgx) to the filterig step in the scanning process. This mode "
                + "\ndoes not perform exactly Map-Reduce operation as it supposed to be but it may be used for sophisticate "
                + "\ntasks. Bear in the mind that the scope of any regular expression is the single 1c event block. It means "
                + "\nthat during the excecution all events beginning with \"tt:ss\" and ending with carret symbol are considered "
                + "\nto be separate expresions. You need to use java syntax for rgx as well. Also notice the node mapped "
                + "\nevents that are matched to the entered user rgx expression only to the corresponded log files. Rgx pattern "
                + "\nare compiled in the DOTALL option."
                + "\n\nMenu:", main);

        MenuCmd m_autoModes = new MenuCmd("4. Auto profiles."
                + "This is a useful list of a everyday logs operations based on kb.1c.ru and 1CFresh servise maintaing routines"
                + "\nSuggest you visit that resources before starting with this mode.", main);

        MenuCmd m_config = new MenuCmd(() -> "5. Configure logcfg.xml."
                + "\nTurns on a log journal to be written and allows you to specify various setting inside logcfg.xml file"
                + "\nThis step should be done before you start using any of log scanning operations because they operate "
                + "\non a logcfg.xml settings created by the correct logcfg.xml file" + getCfgConfigureDescr(), main);

        MenuCmd m_runServer = new MenuCmd("6. Run as server", main);

        //Event handlers

        // Item 1
        main.add(new MenuItemCmd("Cursor log scanning(recommends)", new CmdSetCurrAlgorithm(RgxOpTypes.CURSOR_OP), cursorLogScan));
        MenuCmd menuLogLocCursror = createAddLocMenu(cursorLogScan, true, cursorLogScan);
        cursorLogScan.add(new MenuItemCmd(() -> "SELECT TOP[" + profile.getLimit() + "] FROM location[" + logSize() + "]", null, menuLogLocCursror));
        cursorLogScan.add(new MenuItemCmd("WHERE log type is", new CmdChangeLogType()));
        cursorLogScan.add(new MenuItemCmd("AND WHERE date range is", new CmdChangeLogRange()));
        cursorLogScan.add(new MenuItemCmd("AND WHERE event in", new CmdAddLogEvent()));
        cursorLogScan.add(new MenuItemCmd("AND WHERE event property in", new CmdAddLogFilter()));
        cursorLogScan.add(new MenuItemCmd("GROUP BY", new CmdAddGroupBy()));
        cursorLogScan.add(new MenuItemCmd("ORDER BY", new CmdChangeOrder()));
        cursorLogScan.add(new MenuItemCmd("Reset all", new CmdResetAll()));
        cursorLogScan.add(new MenuItemCmd("Start", new CmdStartCursorOp()));

        // Item 2
        main.add(new MenuItemCmd("Heap log scanning", new CmdSetCurrAlgorithm(RgxOpTypes.HEAP_OP), heapLogScan));
        MenuCmd menuLogLocHeap = createAddLocMenu(heapLogScan, false, heapLogScan);
        heapLogScan.add(new MenuItemCmd(() -> "SELECT FROM location[" + logSize() + "]", null, menuLogLocHeap));
        heapLogScan.add(new MenuItemCmd("WHERE log type is", new CmdChangeLogType()));
        heapLogScan.add(new MenuItemCmd("AND WHERE date range is", new CmdChangeLogRange()));
        heapLogScan.add(new MenuItemCmd("AND WHERE event in", new CmdAddLogEvent()));
        heapLogScan.add(new MenuItemCmd("AND WHERE event property in", new CmdAddLogFilter()));
        heapLogScan.add(new MenuItemCmd("GROUP BY", new CmdAddGroupBy()));
        heapLogScan.add(new MenuItemCmd("Reset all", new CmdResetAll()));
        heapLogScan.add(new MenuItemCmd("Start", new CmdStartMapReduce()));

        // Item 3.
        main.add(new MenuItemCmd("Own rgx log scanning", new CmdSetCurrAlgorithm(RgxOpTypes.USER_OP), m_userEvents));
        MenuCmd menuLogLocUser = createAddLocMenu(m_userEvents, false, m_userEvents);
        m_userEvents.add(new MenuItemCmd(() -> "SELECT FROM location[" + logSize() + "]", null, menuLogLocUser));
        m_userEvents.add(new MenuItemCmd("WHERE log type is", new CmdChangeLogType()));
        m_userEvents.add(new MenuItemCmd("AND WHERE date range is", new CmdChangeLogRange()));
        m_userEvents.add(new MenuItemCmd("AND WHERE regular expression is", new CmdAddRgx()));
        m_userEvents.add(new MenuItemCmd("Reset all", new CmdResetAll()));
        m_userEvents.add(new MenuItemCmd("Start", new CmdStartUserScan()));

        // Item 4.
        main.add(new MenuItemCmd("Auto profiles", null, m_autoModes));
        m_autoModes.add(new MenuItemCmd("Find EXCP from rphost logs grouped by 'descr'", new CmdGetRphostExcp(), heapLogScan));
        m_autoModes.add(new MenuItemCmd("Find EXCP caused by user", new CmdGetUserExcp(), heapLogScan));
        m_autoModes.add(new MenuItemCmd("Find slowest SQL and SDBL events", new CmdGetTopSlowestSql(), cursorLogScan));
        m_autoModes.add(new MenuItemCmd("Find slowest sql by user", new CmdGetTopSlowestSqlByUser(), cursorLogScan));
        m_autoModes.add(new MenuItemCmd("Find slowest sql queries with TABLE SCAN", new CmdGetSQlWithTableScan(), cursorLogScan));
        m_autoModes.add(new MenuItemCmd("Find slowest sql queries with INDEX SCAN", new CmdGetSQlWithIndexScan(), cursorLogScan));
        m_autoModes.add(new MenuItemCmd("Find 1C deadlocks and timeouts", new CmdGetTopTimeout(), cursorLogScan));
        m_autoModes.add(new MenuItemCmd("Find SQL deadlocks and timeouts", new CmdGetSQlLockError(), cursorLogScan));
        m_autoModes.add(new MenuItemCmd("Find deadlocks and timeouts by user'", new CmdGetTopDeadlocksByUser(), cursorLogScan));

        // Item 5.
        main.add(new MenuItemCmd("Configure logcfg.xml", null, m_config));
        m_config.add(new MenuItemCmd("Add new event", new CmdAddEventToCfg(), m_config));
        m_config.add(new MenuItemCmd("Add \"all\" events", new CmdAddAllEventsToCfg(), m_config));
        m_config.add(new MenuItemCmd("Add \"excp\" events", new CmdAddExcpEvents(), m_config));
        m_config.add(new MenuItemCmd("Add \"everyday events\" events", new CmdAddEveryDayEvents(), m_config));
        m_config.add(new MenuItemCmd("Add \"sql\" events", new CmdAddSqlEvent(), m_config));
        m_config.add(new MenuItemCmd("Add \"all sql queries by user\" events", new CmdAddNonEffectiveQueriesEvent(), m_config));
        m_config.add(new MenuItemCmd("Add \"slow sql queries with TABLE SCAN\" events", new CmdAddSlowQueriesWithTableScan(), m_config));
        m_config.add(new MenuItemCmd("Add \"any long\" events gt 20 sec", new CmdAddLongEventsToCfg(), m_config));
        m_config.add(new MenuItemCmd("Add \"1c lock\" events", new CmdAdd1CLockEvents(), m_config));
        m_config.add(new MenuItemCmd("Add \"1c deadlocks\" events", new CmdAdd1cDeadLocksEventsToCfg(), m_config));
        m_config.add(new MenuItemCmd("Add \"sql deadlocks\" events", new CmdAddSqlDeadLocksEventsToCfg(), m_config));
        m_config.add(new MenuItemCmd("Add \"1c table by object id\" events", new CmdAddTableByObjectIdToCfg(), m_config));
        m_config.add(new MenuItemCmd("Remove event", new CmdRemoveEventFromCfg(), m_config));
        m_config.add(new MenuItemCmd("Write to logcfg.xml(NEED ADMINISTRATIVE PRIVILEGES)", new CmdWriteToCfg(), m_config));
        m_config.add(new MenuItemCmd("Copy to the clipboard", new CmdSaveCfgToClipboard(), m_config));

        MenuCmd menuManualCfg = new MenuCmd("Other", m_config);
        m_config.add(new MenuItemCmd("Other", null, menuManualCfg));
        menuManualCfg.add(new MenuItemCmd("Clear all", new CmdClearAllFromCfg(), m_config));
        menuManualCfg.add(new MenuItemCmd("Change location", new CmdChangeLocToCfg(), m_config));
        menuManualCfg.add(new MenuItemCmd("Connect to the other server", new CmdAddLogLocServerIP(), m_config));

        // Item 6.
        main.add(new MenuItemCmd("Run as server", null, m_runServer));
        m_runServer.add(new MenuItemCmd("Run as a lan TCP/IP server", new CmdRunAsLanServer()));
        m_runServer.add(new MenuItemCmd("Run as a fullREST server (not work yet!)", null));

        cmdAppl.setTitle(
                "1CV8 Log Scanner v.0.9"
                        + "\nRuns on " + Constants.osType
                        + "\n********************"
        );
        cmdAppl.runAppl(main);
        clientsManager.resetRemoteClients();

    }

    public ApplConsole getConsole() {
        return cmdAppl;
    }

    public MenuCmd createAddLocMenu(MenuCmd parent, boolean withTopMenu, MenuCmd returnMenu) {
        MenuCmd menu = new MenuCmd(() -> {
            String text = "SELECT FROM location."
                    + "\nList of chosen paths to scan: ";
            if (logSize() == 0)
                text += "\n<Empty>";
            else {
                for (V8LogScannerClient client : clientsManager) {
                    for (String log : client.getProfile().getLogPaths())
                        text = text.concat("\n" + client + " " + log);
                }
            }
            return text;
        }
                , parent);
        menu.add(new MenuItemCmd("Add single log from cfg file", new CmdAddLogLocFromCfg(), returnMenu));
        menu.add(new MenuItemCmd("Add all logs from cfg file", new CmdAddLogLocFromCfgAll(), returnMenu));
        menu.add(new MenuItemCmd("Add own log location", new CmdAddLogLocOwn(), returnMenu));
        menu.add(new MenuItemCmd("Add remote server", new CmdAddLogLocServerIP(), returnMenu));
        if (withTopMenu)
            menu.add(new MenuItemCmd("Set log events limit", new CmdSetTOP(), returnMenu));
        menu.add(new MenuItemCmd("Reset locations", new CmdResetLoc(), returnMenu));

        return menu;
    }

    public int logSize() {
        int sum = 0;
        for (V8LogScannerClient client : clientsManager) {
            sum += client.getProfile().getLogPaths().size();
        }
        return sum;
    }

    public String getFinalInfo() {

        StringBuilder sb = new StringBuilder();

        clientsManager.forEach(client -> sb.append(client.toString() + client.getFinalInfo() + "\n\n"));

        return sb.toString();

    }

    public String getCfgConfigureDescr() {
        if (logBuilder == null) {
            logBuilder = new LogBuilder();
            logBuilder.readCfgFile();
        }

        List<String> pathsDescr = new ArrayList<>();
        List<String> contentDescr = new ArrayList<>();
        clientsManager.forEach(client -> {
            List<String> paths = client.getCfgPaths();
            paths.forEach((path) -> pathsDescr.add("\n" + client + " " + path));
            String content = client.readCfgFile(paths);
            contentDescr.add(client + "\n" + content);
        });

        StringBuilder sb = new StringBuilder();
        sb.append(pathsDescr.size() == 0 ?
                "\n\n1C Enterpise is not installed on a machine(s) (no folders with path \\1cv8\\conf was found)!\n\n" :
                "\n\nFound valid logcfg.xml on:" + String.join("\n", pathsDescr) + "\n\n/***CONTENT***/:\n\n"
        );

        sb.append(String.join("\n", contentDescr));

        int maxLength = 1000;
        if (sb.length() > maxLength) {
            sb.setLength(1000);
            sb.append("...");
        }
        return sb.toString();
    }

    public void resetResult() {
        clientsManager.forEach(client -> client.resetResult());
    }

    public void startRgxOP() {
        cmdAppl.clearConsole();
        clientsManager.startRgxOp();
        showResults();
    }

    public void showResults() {

        MenuCmd showResults = new MenuCmd(() -> String.format("Results:\n%s", getFinalInfo()), null);
        showResults.add(new MenuItemCmd("Show next top 100 keys", new CmdShowResults(SelectDirections.FORWARD), showResults));
        showResults.add(new MenuItemCmd("Show previous top 100 keys", new CmdShowResults(SelectDirections.BACKWARD), showResults));
        showResults.add(new MenuItemCmd("Open the next top 100 keys in the text editor", new CmdCopyResultToFile(), showResults));

        cmdAppl.runAppl(showResults);
        resetResult();
    }

    public void addLogPath(ScanProfile profile, String logPath) {
        List<String> sourceLogPaths = profile.getLogPaths();
        boolean logExist = sourceLogPaths.stream().anyMatch(n -> n.compareTo(logPath) == 0);
        if (!logExist) {
            sourceLogPaths.add(logPath);
        }
    }

    public String askUserName() {
        String[] msg = {"input user name (for example DefUser):"};
        return getConsole().askInput(
                msg,
                (input) -> input.matches("\\S+"),
                true
        );
    }

    public V8LogScannerClient askServer() {
        List<V8LogScannerClient> clients = clientsManager.getClients();
        // Choose server which events receive
        String userInput = getConsole().askInputFromList("input numeric index to choose server which events receive:",
                clients);
        if (userInput == null)
            return null;
        return clients.get(Integer.parseInt(userInput));
    }

    // for testing purposes
    public void setApplConsole(ApplConsole cmdAppl, ClientsManager manager) {
        this.cmdAppl = cmdAppl;
        this.clientsManager = manager;
    }
}
