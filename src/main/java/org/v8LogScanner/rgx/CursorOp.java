package org.v8LogScanner.rgx;

import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.rgx.IRgxSelector.SelectDirections;
import org.v8LogScanner.rgx.RegExp.PropTypes;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class CursorOp extends AbstractOp {

    private CursorSelector selector = new CursorSelector();
    private long totalIn = 0;
    private long totalOut = 0;
    private long afterSlize = 0;
    private long totalKeys = 0;

    // profile variables
    private int limit;
    private List<RegExp> rgxList;
    private PropTypes sortingProp;

    public CursorOp(ScanProfile profile) {
        this.limit = profile.getLimit();
        this.rgxList = profile.getRgxList();
        this.sortingProp = profile.getSortingProp();
    }

    // INTERFACE

    public void execute(List<String> logFiles) {

        calc.start();

        saveProcessingInfo("\n*START CURSOR LOG SCANNING...");

        precompile(rgxList);

        List<String> reducedLogs = new ArrayList<>();
        Collection<String> syncReduced = Collections.synchronizedCollection(reducedLogs);

        for (int i = 0; i < logFiles.size(); i++) {
            try (RgxReader reader = new RgxReader(logFiles.get(i), Constants.logsCharset, Constants.logEventsCount)) {
                ArrayList<String> readResult;
                while (reader.next()) {
                    readResult = reader.getResult();
                    inSize += readResult.size();
                    TreeMap<SortingKey, List<String>> mapResults = mapLogs(readResult);
                    int out = mapResults.entrySet().stream().mapToInt(n -> n.getValue().size()).sum();
                    outSize += Math.min(out, limit);
                    intermediateReduction(syncReduced, mapResults);
                }
            } catch (IOException e) {
                ExcpReporting.LogError(this.getClass(), e);
            }
            saveProcessingInfo(String.format("in: %s, out: %s, %s", inSize, outSize, logFiles.get(i)));
            totalIn += inSize;
            totalOut += outSize;
            inSize = 0;
            outSize = 0;
        }
        afterSlize = syncReduced.size();
        TreeMap<SortingKey, List<String>> rgxResult = finalReduction(syncReduced);
        totalKeys = rgxResult.keySet().size();
        selector.setResult(rgxResult);
        calc.end();
    }

    public void resetResult() {
        totalIn = 0;
        totalOut = 0;
        selector.clearResult();
        eventPatterns.clear();
        inSize = 0;
        outSize = 0;
        processingInfo.clear();
        afterSlize = 0;
    }

    public String getFinalInfo(String logDescr) {

        return String.format(
                "\nSummary:"
                        + "\n Total scanned log files: %s"
                        + "\n Total in events: %s"
                        + "\n Total out events: %s"
                        + "\n Final amount of events: %s reduced to limit: %s"
                        + "\n Total keys: %s"
                        + "\n Execution time: %s",
                logDescr,
                totalIn,
                totalOut,
                afterSlize,
                Math.min(limit, afterSlize),
                totalKeys,
                calc.getTime());
    }

    public List<SelectorEntry> select(int count, SelectDirections direction) {
        return selector.select(count, direction);
    }

    public int cursorIndex() {
        return selector.cursorIndex();
    }

    // PRIVATE

    private TreeMap<SortingKey, List<String>> mapLogs(ArrayList<String> sourceCol) {

        TreeMap<SortingKey, List<String>> mapped =
                sourceCol.
                        stream().
                        parallel().
                        filter(n -> RgxOpManager.anyMatch(n, eventPatterns, integerFilters, integerCompTypes)).
                        collect(Collectors.collectingAndThen(
                                Collectors.groupingByConcurrent(this::createSortingKey),
                                map -> {
                                    TreeMap<SortingKey, List<String>> tm = new TreeMap<>();
                                    map.entrySet().forEach(n -> tm.put(n.getKey(), n.getValue()));
                                    return tm;
                                }
                        ));

        return mapped;

    }

    private void intermediateReduction(Collection<String> syncReduced, TreeMap<SortingKey, List<String>> mapped) {

        mapped.entrySet().
                stream().
                sequential().
                flatMap(n -> n.getValue().stream()).
                limit(limit).
                forEach(n -> syncReduced.add(n));

    }

    private TreeMap<SortingKey, List<String>> finalReduction(Collection<String> sourceCol) {

        TreeMap<SortingKey, List<String>> mapped =
                sourceCol.
                        parallelStream().
                        collect(Collectors.collectingAndThen(
                                Collectors.groupingByConcurrent(this::createSortingKey),
                                map -> {
                                    TreeMap<SortingKey, List<String>> tm = new TreeMap<>();
                                    map.entrySet().forEach(n -> tm.put(n.getKey(), n.getValue()));
                                    return tm;
                                })
                        );

        TreeMap<SortingKey, List<String>> rgxResult = mapped.entrySet().
                stream().
                sequential().
                flatMap(n -> n.getValue().stream()).
                limit(limit).
                collect(Collectors.collectingAndThen(
                        Collectors.groupingByConcurrent(this::createSortingKey),
                        map -> {
                            TreeMap<SortingKey, List<String>> tm = new TreeMap<>();
                            map.entrySet().forEach(n -> tm.put(n.getKey(), n.getValue()));
                            return tm;
                        })
                );

        return rgxResult;
    }

    private SortingKey createSortingKey(String input) {

        String eventKey = RgxOpManager.getEventProperty(
                input, eventPatterns, cleanPropsRgx, groupPropsRgx);

        BigInteger sortingKey = RgxOpManager.getSortingProp(input, sortingProp);

        if (sortingProp == PropTypes.Duration)
            return new DurationKey(eventKey, sortingKey);
        else
            return new SizeKey(eventKey, sortingKey);
    }

    ////////////////////////////////////////////////
    // SORTING CLASSES /////////////////////////////
    ////////////////////////////////////////////////

    public interface SortingKey extends Comparable<SortingKey> {

        public String getKey();

        public BigInteger getSortingKey();

        public void setSortingKey(BigInteger sortingKey);

        public int compareTo(SortingKey o);

    }

    public class DurationKey implements SortingKey {

        final String key;
        private BigInteger sortingKey;

        public DurationKey(String key, BigInteger sortingKey) {
            this.key = key;
            this.sortingKey = sortingKey;
        }

        public String getKey() {
            return key;
        }

        public BigInteger getSortingKey() {
            return sortingKey;
        }

        public void setSortingKey(BigInteger sortingKey) {
            this.sortingKey = sortingKey;
        }

        @Override
        public int compareTo(SortingKey o) {
            int comparedVal = sortingKey.compareTo(o.getSortingKey());
            return -comparedVal;
        }

        public int hashCode() {
            return sortingKey.hashCode();
        }

        public boolean equals(Object x) {
            DurationKey other = (DurationKey) x;
            return sortingKey.equals(other.getSortingKey());
        }

        @Override
        public String toString() {

            long oneHour = 3600000000l;
            long oneMin = 60000000;
            long oneSec = 1000000;

            long overall_msec = sortingKey.longValue();

            long hours = overall_msec / oneHour;
            long min = (overall_msec - hours * oneHour) / oneMin;
            long sec = (overall_msec - hours * oneHour - min * oneMin) / oneSec;
            long msec = overall_msec - hours * oneHour - min * oneMin - sec * oneSec;

            return String.format("DURATION: %sh %sm %ss %sms,\nGROUP: %s", hours, min, sec, msec, key);

        }
    }

    public class SizeKey implements SortingKey {

        final String key;
        private BigInteger sortingKey;

        public SizeKey(String key, BigInteger sortingKey) {
            this.key = key;
            this.sortingKey = sortingKey;
        }

        public String getKey() {
            return key;
        }

        public BigInteger getSortingKey() {
            return sortingKey;
        }

        public void setSortingKey(BigInteger sortingKey) {
            this.sortingKey = sortingKey;
        }

        @Override
        public int compareTo(SortingKey o) {
            int sizeComparator = sortingKey.compareTo(o.getSortingKey());
            if (sizeComparator == 0)
                return -1;
            return -sizeComparator;
        }

        //Both method as hashCode() and equals() is used to group objects in collectors framework
        public int hashCode() {
            return key.hashCode();
        }

        public boolean equals(Object x) {

            if (x == this)
                return true;

            if (!(x instanceof SizeKey))
                return false;

            SizeKey sortX = (SizeKey) x;
            if (key.compareTo(sortX.getKey()) != 0)
                return false;

            BigInteger otherNumb = sortX.getSortingKey();
            this.sortingKey = sortingKey.add(otherNumb);
            sortX.setSortingKey(this.sortingKey);

            return true;
        }

        public String toString() {
            return key;
        }
    }

}
