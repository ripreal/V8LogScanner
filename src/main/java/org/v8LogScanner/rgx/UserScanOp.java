package org.v8LogScanner.rgx;

import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserScanOp extends AbstractOp {

    private HeapSelector selector = new HeapSelector();
    private Pattern pattern = null;
    private int totalEvents = 0;

    //profile variables
    private String rgxExp;

    UserScanOp(ScanProfile profile) {
        this.rgxExp = profile.getRgxExp();
    }

    // INTERFACE

    public void execute(List<String> logFiles) {

        calc.start();

        saveProcessingInfo("\n*START OWN RGX LOG SCANNING...");

        resetResult();

        pattern = Pattern.compile(rgxExp, Pattern.DOTALL);

        ConcurrentMap<String, List<String>> rgxResult = new ConcurrentHashMap<>();

        for (String logFile : logFiles) {
            ArrayList<String> mapLogs = new ArrayList<>();
            try (RgxReader reader = new RgxReader(logFile, Constants.logsCharset, Constants.logEventsCount)) {

                ArrayList<String> readResult;

                while (reader.next()) {
                    readResult = reader.getResult();

                    inSize += readResult.size();
                    mapLogs.addAll(filterLogs(readResult));
                    outSize += mapLogs.size();
                    totalEvents += mapLogs.size();
                }

                if (mapLogs.size() > 0) {
                    rgxResult.put(logFile, mapLogs);
                }
            } catch (IOException e) {
                ExcpReporting.LogError(this.getClass(), e);
            }
            saveProcessingInfo(String.format("in: %s, out: %s, %s", inSize, outSize, logFile));
            inSize = 0;
            outSize = 0;
        }
        selector.setResult(rgxResult);
        calc.end();
    }

    private List<String> filterLogs(ArrayList<String> mapList) {

        return mapList.
                parallelStream().
                unordered().
                filter(n -> pattern.matcher(n).matches()).
                sequential().
                sorted(RgxOpManager::compare).
                collect(Collectors.toList());
    }

    private void resetResult() {
        selector.clearResult();
        processingInfo.clear();
        inSize = 0;
        outSize = 0;
    }

    public String getFinalInfo(String logDescr) {
        ConcurrentMap<String, List<String>> rgxResult = selector.getResult();
        return String.format(
                "\nSUMMARY:"
                        + "\n Total scanned log files: %s"
                        + "\n Total log files with matched events: %s"
                        + "\n Total matched events: %s"
                        + "\n Execution time: %s", logDescr, rgxResult.size(), totalEvents, calc.getTime());
    }

    @Override
    public boolean hasResult() {
        return selector != null;
    }

    public List<SelectorEntry> select(int count, SelectDirections direction) {
        return selector.select(count, direction);
    }

    public int cursorIndex() {
        return selector.cursorIndex();
    }
}
