package org.v8LogScanner.rgx;

import org.v8LogScanner.commonly.PerfCalc;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;

import java.util.ArrayList;
import java.util.List;

public interface IRgxOp {

    public final PerfCalc calc = new PerfCalc();

    public void execute(List<String> logFiles);

    public ArrayList<String> getProcessingInfo();

    public String getFinalInfo(String logDescr);

    public List<SelectorEntry> select(int count, SelectDirections direction);

    public int cursorIndex();

    public void addListener(ProcessEvent e);

}
