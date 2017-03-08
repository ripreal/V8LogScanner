package org.v8LogScanner.cmdScanner;

import java.util.List;
import org.v8LogScanner.LocalTCPLogScanner.ClientsManager;
import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.LocalTCPLogScanner.V8LogScannerClient;
import org.v8LogScanner.cmdAppl.ApplConsole;
import org.v8LogScanner.cmdAppl.MenuCmd;
import org.v8LogScanner.cmdAppl.MenuItemCmd;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.ScanProfile;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;
//DOMAIN SPECIFIC CONSOLE AND ITS METHODS

public class V8LogScannerAppl {
  
  public final ClientsManager clientsManager;
  public ScanProfile profile;
  
	private MenuCmd main;
	private static V8LogScannerAppl instance;
	private ApplConsole cmdAppl; 
	
	//subscription to the rgx and logs events 
	public final ProcessEvent procEvent = new ProcessEvent(){
		public void invoke(List<String> info) {
			cmdAppl.showInfo(info);
		}
	};
	
	public static V8LogScannerAppl instance(){
	  
		if (instance == null){
			instance = new V8LogScannerAppl(new ApplConsole());
		}
		return instance;
	}
	
	public static void main(String[] args){
		
		ExcpReporting.out = System.out;
		
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		appl.runAppl();
		
	}
	
	private V8LogScannerAppl(ApplConsole appl){
		
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
		
		MenuCmd m_runServer = new MenuCmd("5. Run as server", main);
		
		//Event handlers
		
		// Item 1
		main.add(new MenuItemCmd("Cursor log scanning(recommends)", new CmdSetCurrAlgorithm(RgxOpTypes.CURSOR_OP), cursorLogScan));
		MenuCmd menuLogLocCursror = createAddLocMenu(cursorLogScan, true);
		cursorLogScan.add(new MenuItemCmd(() -> "SELECT TOP["+ profile.getLimit()+"] FROM location[" + logSize() + "]", null, menuLogLocCursror));
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
		MenuCmd menuLogLocHeap = createAddLocMenu(heapLogScan, false);
		heapLogScan.add(new MenuItemCmd(() -> "SELECT FROM location[" + logSize() +"]", null, menuLogLocHeap));
		heapLogScan.add(new MenuItemCmd("WHERE log type is", new CmdChangeLogType()));
		heapLogScan.add(new MenuItemCmd("AND WHERE date range is", new CmdChangeLogRange()));
		heapLogScan.add(new MenuItemCmd("AND WHERE event in", new CmdAddLogEvent()));
		heapLogScan.add(new MenuItemCmd("AND WHERE event property in", new CmdAddLogFilter()));
		heapLogScan.add(new MenuItemCmd("GROUP BY", new CmdAddGroupBy()));
		heapLogScan.add(new MenuItemCmd("Reset all", new CmdResetAll()));
		heapLogScan.add(new MenuItemCmd("Start", new CmdStartMapReduce()));
		
		// Item 3.
		main.add(new MenuItemCmd("Own rgx log scanning", new CmdSetCurrAlgorithm(RgxOpTypes.USER_OP), m_userEvents));
		MenuCmd menuLogLocUser = createAddLocMenu(m_userEvents, false);
		m_userEvents.add(new MenuItemCmd(() -> "SELECT FROM location[" + logSize() +"]", null, menuLogLocUser));
		m_userEvents.add(new MenuItemCmd("WHERE log type is", new CmdChangeLogType()));
		m_userEvents.add(new MenuItemCmd("AND WHERE date range is", new CmdChangeLogRange()));
		m_userEvents.add(new MenuItemCmd("AND WHERE regular expression is", new CmdAddRgx()));
		m_userEvents.add(new MenuItemCmd("Reset all", new CmdResetAll()));
		m_userEvents.add(new MenuItemCmd("Start", new CmdStartUserScan()));
		
		// Item 4.
		main.add(new MenuItemCmd("Auto profiles", null, m_autoModes));
		m_autoModes.add(new MenuItemCmd("Find EXCP from rphost logs grouped by 'descr'", new CmdGetRphostExcp(), heapLogScan));
		m_autoModes.add(new MenuItemCmd("Find top slowest SQL and SDBL events grouped by 'Context'", new CmdGetTopSlowestSql(), cursorLogScan));
		m_autoModes.add(new MenuItemCmd("Find top slowest sql texts", new CmdGetTopSlowestSqlText(), cursorLogScan));
		m_autoModes.add(new MenuItemCmd("Find top most frequent TTIMEOUT events (not finished)'", new CmdGetTopTimeout(), cursorLogScan));
		
		// Item 5.
		main.add(new MenuItemCmd("Run as server", null, m_runServer));
		m_runServer.add(new MenuItemCmd("Run as lan TCP/IP server", new CmdRunAsLanServer()));
		m_runServer.add(new MenuItemCmd("Run as fullREST server (not work yet!)", null));
		
		cmdAppl.setTitle(
      "1CV8 Log Scanner v.0.9"
      +"\nRuns on " + Constants.osType
      +"\n********************"
    );
		cmdAppl.runAppl(main);
		
		clientsManager.resetRemoteClients();
		
	}
	
	public ApplConsole getConsole() {
	  return cmdAppl;
	}
	
	public MenuCmd createAddLocMenu(MenuCmd parent, boolean withTopMenu){
		MenuCmd menu = new MenuCmd(() -> {
			String text = "SELECT FROM location."
				+ "\nList of chosen paths to scan: ";
			if (logSize() == 0) 
				text += "\n<Empty>";
			else{
				for (V8LogScannerClient client : clientsManager){
					for(String log : client.getProfile().getLogPaths())
						text = text.concat("\n" + client +" " + log);
				}
			}
			return text;
			}
		, parent);
		menu.add(new MenuItemCmd("Add single log from cfg file", new CmdAddLogLocFromCfg(), menu));
		menu.add(new MenuItemCmd("Add All logs from cfg file", new CmdAddLogLocFromCfgAll(), menu));
		menu.add(new MenuItemCmd("Add own log location", new CmdAddLogLocOwn(), menu));
		menu.add(new MenuItemCmd("Add remote server", new CmdAddLogLocServerIP(), menu));
		if (withTopMenu)
			menu.add(new MenuItemCmd("Set log events limit", new CmdSetTOP(), menu));
		menu.add(new MenuItemCmd("Reset log", new CmdResetLoc(), menu));
		
		return menu;
	}

	public int logSize(){
	  int sum = 0;
	  for(V8LogScannerClient client : clientsManager) {
	    sum += client.getProfile().getLogPaths().size();
	  }
	  int sum2 = clientsManager.getClients().get(0).getProfile().getLogPaths().size();
	  int sum1 = V8LogScannerAppl.instance().clientsManager.getClients().get(0).getProfile().getLogPaths().size();
		//int sum =  .stream().mapToInt(n -> n.getProfile().getLogPaths().size()).sum();
		return sum;
	}
	
	public String getFinalInfo(){
		
		StringBuilder sb = new StringBuilder(); 
		
		clientsManager.forEach(client -> sb.append(client.toString() + client.getFinalInfo() + "\n\n"));
		
		return sb.toString();
		
	}

	public void resetResult(){
		clientsManager.forEach(client -> client.resetResult());
	}
	
	public void startRgxOP() {
		
		clientsManager.forEach(client -> {
			LanScanProfile lanProfile = (LanScanProfile) profile;
			ScanProfile cloned = lanProfile.clone();
			cloned.setLogPaths(client.getProfile().getLogPaths());
			client.setProfile(cloned);
		});
		
		cmdAppl.clearConsole();
		clientsManager.forEach(client -> client.startRgxOp());
		showResults();
		
	} 
	
	public void showResults(){
		
		MenuCmd showResults = new MenuCmd(() -> String.format("Results:\n%s", getFinalInfo()), null);
		showResults.add(new MenuItemCmd("Show next top 100 keys", new CmdShowResults(true), showResults));
		showResults.add(new MenuItemCmd("Show previous top 100 keys", new CmdShowResults(false), showResults));
		
		cmdAppl.runAppl(showResults);
		resetResult();
	}
	
	public void addLogPath(ScanProfile profile, String logPath){
		List<String> sourceLogPaths = profile.getLogPaths();
		boolean logExist = sourceLogPaths.stream().anyMatch(n -> n.compareTo(logPath) == 0);
		if (!logExist){
			sourceLogPaths.add(logPath);
		}
	}
	
	// for testing purposes
	public void setApplConsole(ApplConsole cmdAppl) {
	  this.cmdAppl = cmdAppl;
	}
	
}
