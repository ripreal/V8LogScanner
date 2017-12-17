package org.v8LogScanner.rgx;

import java.util.ArrayList;
import java.util.List;

public interface IRgxSelector {

    enum SelectDirections {FORWARD, BACKWARD}
    
    List<SelectorEntry> select(int count, SelectDirections direction);

    int cursorIndex();

    void clearResult();

}
