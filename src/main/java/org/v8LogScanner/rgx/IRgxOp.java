package org.v8LogScanner.rgx;

import org.v8LogScanner.commonly.PerfCalc;
import org.v8LogScanner.commonly.ProcessEvent;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;

import java.util.ArrayList;
import java.util.List;

public interface IRgxOp {

    PerfCalc calc = new PerfCalc();

    void execute(List<String> logFiles);

    ArrayList<String> getProcessingInfo();

    String getFinalInfo(String logDescr);

    boolean hasResult();

    List<SelectorEntry> select(int count, SelectDirections direction);

    int cursorIndex();

    void addListener(ProcessEvent e);

}
