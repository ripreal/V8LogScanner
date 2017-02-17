package org.v8LogScanner.cmdScanner;

import java.util.List;

import org.v8LogScanner.LocalTCPLogScanner.LanScanProfile;
import org.v8LogScanner.cmdAppl.CmdCommand;
import org.v8LogScanner.rgx.RegExp;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;
import org.v8LogScanner.rgx.ScanProfile.RgxOpTypes;

public class CmdGetTopSlowestSqlText implements CmdCommand {

	@Override
	public String getTip() {
		return "";
	}

	@Override
	public void execute() {
		
		V8LogScannerAppl appl = V8LogScannerAppl.instance();
		
		appl.profile = new LanScanProfile(RgxOpTypes.CURSOR_OP);
		appl.clientsManager.localClient().setProfile(appl.profile);
		
		List<RegExp> rgxList = appl.profile.getRgxList();
		
		RegExp dbmsql = new RegExp(EventTypes.DBMSSQL);
		dbmsql.getGroupingProps().add(PropTypes.Sql);
		dbmsql.getFilter(PropTypes.Sql).add(""); // only seek events with present prop
		
		rgxList.add(dbmsql);
		
		RegExp sdbl = new RegExp(EventTypes.SDBL);
		sdbl.getGroupingProps().add(PropTypes.Sdbl);
		sdbl.getFilter(PropTypes.Sdbl).add(""); // only seek events with present prop
		rgxList.add(sdbl);
		
		RegExp dbv8d8eng = new RegExp(EventTypes.DBV8DBEng);
		dbv8d8eng.getGroupingProps().add(PropTypes.Sql);
		dbv8d8eng.getFilter(PropTypes.Sql).add(""); // only seek events with present prop
		rgxList.add(dbv8d8eng);
		
		appl.profile.setSortingProp(PropTypes.Duration);
		
	}

}
