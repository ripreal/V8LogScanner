package org.v8LogScanner.commonly;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.v8LogScanner.rgx.StrokeFilter;
import org.v8LogScanner.rgx.TimeFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY,  property = "type", defaultImpl = StrokeFilter.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StrokeFilter.class, name = "StrokeFilter"),

        @JsonSubTypes.Type(value = TimeFilter.class, name = "TimeFilter") }
)
public abstract class Filter implements Iterable<String>, Serializable {

    private static final long serialVersionUID = 7463203729935217783L;
    private List<String> elements = new ArrayList<>();
    private ComparisonTypes comparisonType = ComparisonTypes.equal;

    public enum ComparisonTypes {equal, greater, less, range}

    public ComparisonTypes getComparisonType() {
        return comparisonType;
    }

    public void setComparisonType(ComparisonTypes comparisonType) {
        this.comparisonType = comparisonType;
    }

    public Filter add(String val) {
        elements.add(val);
        return this;
    }

    public boolean isActive() {
        return elements.size() > 0;
    }

    public void reset() {
        elements.clear();
    }

    public int size() {
        return elements.size();
    }

    public String get(int index) {
        return elements.get(index);
    }

    public Iterator<String> iterator() {
        return elements.iterator();
    }

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }

}