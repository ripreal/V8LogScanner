package org.v8LogScanner.rgx;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.v8LogScanner.commonly.Filter;

@JsonSubTypes.Type(value = StrokeFilter.class)
public class StrokeFilter extends Filter {

    /**
     *
     */
    private static final long serialVersionUID = 2027201370278979033L;

    @Override
    public String toString() {
        String comp = getComparisonType().toString();
        return comp + "_" + (getElements().contains("") ? "ANY" : String.join(",", getElements()));
    }
}

