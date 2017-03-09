package org.v8LogScanner.rgx;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.v8LogScanner.commonly.Strokes;
import org.v8LogScanner.commonly.Filter.ComparisonTypes;
import org.v8LogScanner.rgx.RegExp.EventTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;

public class RgxOpManager{
  
  // GENERAL PATTERN FOR CAPTURE EVENT BLOCK
  // (?ms)^(\d{2}:\d{2}\.\d+-\d+,(EVENT)).+?(?=\d{2}:\d{2})
  //      ^(\d{2}:\d{2}\.\d+-\d+,(EXCP)).+?(?=\d{2}:\d{2})
  
  private static Pattern durationPattern = Pattern.compile("(\\d{2}:\\d{2}\\.\\d+-)(\\d+)");
  private static Pattern timePattern = Pattern.compile("\\d{2}:\\d{2}");
      
  /**
   * Compare time properties in two logs events. 
   * Can be used as function with Comparator() interface
   * If var1 > var2 return greater than 0
   * If var1 < than var2 return less than 0
   * If var1 = var2 return 0;
  **/
  public static int compare(String var1, String var2){
    
    if (!(Strokes.isNumericAtIndex(var1, 0) && Strokes.isNumericAtIndex(var2, 0)))
        return 0;
    
    String time1 = var1.substring(0, 12);
    String time2 = var2.substring(0, 12);

    char oldChars[] = new char[2];
    oldChars[0] = ':';
    oldChars[1] = '.';
    time1 = deleteChars(time1, oldChars);
    time2 = deleteChars(time2, oldChars);
    
    long timeD1 = Long.parseLong(time1);
    long timeD2 = Long.parseLong(time2);
    
    return Long.compare(timeD1, timeD2);
  }
  
  /**
   * Delete found chars from the source string. 
   */
  public static String deleteChars(String input, char[] oldChars){
    
    char[] chars = input.toCharArray();

    String result = "";
    for (int i=0; i<chars.length; i++){
      
      char c = chars[i];
      
      boolean skip = false;
      for (int j =0; j< oldChars.length; j++)
        if (c == oldChars[j]){
          skip = true;
          break;
        }
        
      if (!skip)
        result = result.concat(String.valueOf(c));
    }
    return result;
  }
  
  /**
   * obtains desired property from a single event block. 
   * Method signature is suitable for RgxGrouper function
   */
  public static String getEventProperty(String input, ConcurrentMap<EventTypes, Pattern> eventPatterns,
      ConcurrentMap<EventTypes, List<String>> cleanProps, ConcurrentMap<EventTypes, List<String>> groupProps){
    
    EventTypes foundEvent = anyMatch(input, eventPatterns);
    if (foundEvent == null)
      return input;
    
    if (cleanProps.size() > 0)
      input = cleanEventProperty(input, foundEvent, cleanProps);
    
    List<String> propsRgx = groupProps.get(foundEvent);
    
    if (propsRgx == null)
      return input;
    
    String obtainedProps = "";
    
    for(String currRgx : propsRgx){
      Pattern pattern = Pattern.compile(currRgx);
      Matcher matcher = pattern.matcher(input);
      if (matcher.find())
        obtainedProps += matcher.group().concat(",");
    }
    return obtainedProps;
  }
  
  /**
   * Delete properties in the input event that are not allow to accomplish grouping. 
   * Method signature is suitable for RgxGrouper function
   * @param input
   * @param rgxList
   * @return
   */
  private static String cleanEventProperty(String input, 
      EventTypes foundEvent, ConcurrentMap<EventTypes, List<String>> props){
    
    List<String> unicRgx = props.get(foundEvent);
    
    if (unicRgx == null)
      return input;
      
    for(String currRgx : unicRgx){
      String regex = currRgx + ",?";
      input = input.replaceFirst(regex, "");
    }
    
    return input;
  }
  
  /**
   * Matches input string against any of RexExp from List.
   * Can be used as function with Collector() interface
   * @param input
   * @param rgxList
   * @return boolean
   */
  public static boolean anyMatch(String input, 
      ConcurrentMap<EventTypes, Pattern> eventPatterns,
      ConcurrentMap<EventTypes, ConcurrentMap<PropTypes, List<String>>> integerFilters, 
      ConcurrentMap<EventTypes, ConcurrentMap<PropTypes, ComparisonTypes>> integerCompTypes){
    
    boolean isMatch = false;
    EventTypes foundEvent = anyMatch(input, eventPatterns);
    
    if (foundEvent != null){
      isMatch = true;
      // integer props (Time, duration etc) are not filtered by rgx pattern. Use own filter.
      ConcurrentMap<PropTypes, List<String>> props = integerFilters.get(foundEvent);
      ConcurrentMap<PropTypes, ComparisonTypes> compTypes = integerCompTypes.get(foundEvent);
      
      if (props == null)
        // When integer filters are empty.
        return isMatch;
      
      Set<PropTypes> keysProps = props.keySet();
      for(PropTypes prop : keysProps)
        if (!filterIntegers(input, prop, props.get(prop), compTypes.get(prop)))
        {
          // one of the integer props (Time, duration, etc) 
          // does not matches input event;
          isMatch = false;
          break;
        }
      }
    return isMatch;
  }
  
  public static EventTypes anyMatch(String input, ConcurrentMap<EventTypes, Pattern> eventPatterns){
    
    EventTypes foundEvent = null;
    
    Matcher matcher;
    Set<EventTypes> keys = eventPatterns.keySet();
    for (EventTypes event : keys){
      Pattern pattern = eventPatterns.get(event);
      matcher = pattern.matcher(input);
      if (matcher.find()){
        foundEvent = event;
        break;
      }
    }
    return foundEvent;
  }
  
  private static boolean filterIntegers(String input, PropTypes prop, List<String>  filterVals, ComparisonTypes compType){
    
    boolean isMatch = true;
    BigInteger inputProp = new BigInteger("0");
    if(prop == PropTypes.Time)
      inputProp = getTime(input);
    else if(prop == PropTypes.Duration)
      inputProp = getDuration(input);
    
    isMatch = MatchesIntFilter(inputProp, filterVals, compType);
    return isMatch;
  }
  
  private static boolean MatchesIntFilter(BigInteger inputVal, List<String> filterVals, ComparisonTypes compType){
      boolean isMatch = true;

      if (compType == ComparisonTypes.range){
        BigInteger filterVal1 = new BigInteger (filterVals.get(0));
        BigInteger filterVal2 = new BigInteger(filterVals.get(1));
        
        if (filterVal1.compareTo(filterVal2) > 0){
          BigInteger filterVal3 = filterVal2;
          filterVal2 = filterVal1;
          filterVal1 = filterVal3;
        }
        
        if ( inputVal.compareTo(filterVal2) >= 0 && inputVal.compareTo(filterVal1) <= 0)
          isMatch = true;
        else
          isMatch = false;
      }
      else{
        BigInteger filterVal = new BigInteger(filterVals.get(0));

        if (compType == ComparisonTypes.equal)
          isMatch = (filterVal.compareTo(inputVal) == 0);
        else if(compType == ComparisonTypes.greater)
          isMatch = (filterVal.compareTo(inputVal) <= 0);
        else if(compType == ComparisonTypes.less)
          isMatch = (filterVal.compareTo(inputVal) >= 0);
      }
      return isMatch;
    }
  
  public static boolean isNumericProp(PropTypes prop){
    return RegExp.isNumericProp(prop);
  }
  
  public static BigInteger getSortingProp(String input, PropTypes propType){
    
    BigInteger sortingKey = null;
    if (propType == PropTypes.Duration)
      sortingKey = getDuration(input);
    else if (propType == PropTypes.Time)
      sortingKey = getTime(input);
    else
      sortingKey = new BigInteger("1");
    
    return sortingKey;
  }
  
  public static BigInteger getDuration(String input){
    BigInteger duration = new BigInteger("0");
    Matcher durationMatcher = durationPattern.matcher(input);
    if (durationMatcher.find())
      duration = new BigInteger(durationMatcher.group(2));
    return duration;
  }
  
  public static BigInteger getTime(String input){
    BigInteger time = new BigInteger("0");
    Matcher timeMatcher = timePattern.matcher(input);
    if (timeMatcher.find())
      time = new  BigInteger(timeMatcher.group().replace(":", ""));
    return time;
  }

}
