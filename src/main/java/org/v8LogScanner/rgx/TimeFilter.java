package org.v8LogScanner.rgx;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.v8LogScanner.commonly.Filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@JsonSubTypes.Type(value = TimeFilter.class)
public class TimeFilter extends Filter {

    private static final long serialVersionUID = 8294945164173926841L;

    @Override
    public Filter add(String val) {
        getElements().add(fetchProp(val));
        return this;
    }

    private String fetchProp(String userInput) {
        Matcher timeMatcher = Pattern.compile("\\d{2}:\\d{2}").matcher(userInput);
        if (timeMatcher.find()) {
            userInput = timeMatcher.group().replace(":", "");
        }
        return userInput;
    }

    @Override
    public String toString() {
        String comp = getComparisonType().toString();
        return comp + "_" + (getElements().contains("") ? "ANY" : String.join(",", getElements()));
    }
}

