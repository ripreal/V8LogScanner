package org.v8LogScanner.commonly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Filter<T1> implements Iterable<T1>, Serializable {

    private static final long serialVersionUID = 7463203729935217783L;
    private List<T1> elements = new ArrayList<>();
    private ComparisonTypes comparisonType = ComparisonTypes.equal;

    public enum ComparisonTypes {equal, greater, less, range}

    ;

    public ComparisonTypes getComparisonType() {
        return comparisonType;
    }

    ;

    public void setComparisonType(ComparisonTypes comparisonType) {
        this.comparisonType = comparisonType;
    }

    ;

    public Filter<T1> add(T1 val) {
        elements.add(val);
        return this;
    }

    ;

    public boolean isActive() {
        return elements.size() > 0;
    }

    ;

    public void reset() {
        elements.clear();
    }

    ;

    public int size() {
        return elements.size();
    }

    ;

    public T1 get(int index) {
        return elements.get(index);
    }

    ;

    public Iterator<T1> iterator() {
        return elements.iterator();
    }

    ;

    public List<T1> getElements() {
        return elements;
    }

    ;

    public void setElements(List<T1> elements) {
        this.elements = elements;
    }

    ;

}