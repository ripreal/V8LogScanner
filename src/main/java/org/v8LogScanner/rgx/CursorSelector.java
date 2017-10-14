package org.v8LogScanner.rgx;

import org.v8LogScanner.rgx.CursorOp.SortingKey;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CursorSelector implements IRgxSelector {

    private int cursorIndex = 0;

    private TreeMap<SortingKey, List<String>> rgxResult = new TreeMap<>();

    public void setResult(TreeMap<SortingKey, List<String>> result) {
        rgxResult = result;
    }

    public TreeMap<SortingKey, List<String>> getResult() {
        return rgxResult;
    }

    public List<SelectorEntry> select(int count, SelectDirections direction) {

        if (direction == SelectDirections.FORWARD && cursorIndex == rgxResult.size() - 1)
            cursorIndex = 0;

        if (direction == SelectDirections.BACKWARD && cursorIndex == 0)
            cursorIndex = Math.max(rgxResult.size() - count, 0);
        else if (direction == SelectDirections.BACKWARD) {
            count = Math.min(cursorIndex, count);
            cursorIndex = cursorIndex - count;
        }

        List<SelectorEntry> selected = new ArrayList<>();

        rgxResult.entrySet().
                stream().
                skip(cursorIndex).
                limit(count).forEachOrdered(entry -> {
            SelectorEntry row = new SelectorEntry(entry.getKey().toString(), entry.getValue());
            selected.add(row);
        });

        if (direction == SelectDirections.FORWARD)
            cursorIndex = Math.min(cursorIndex + selected.size(), rgxResult.size() - 1);

        if (cursorIndex < 0)
            cursorIndex = 0;

        return selected;
    }

    public int cursorIndex() {
        return cursorIndex;
    }

    public void clearResult() {
        rgxResult.clear();
        cursorIndex = 0;
    }

}
