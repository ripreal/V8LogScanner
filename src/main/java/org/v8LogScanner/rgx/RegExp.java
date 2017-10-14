/////////////////////////////////////
//INTERPRETER PATTERN 
// THESE CLASSES ARE NOT THREAD-SAFE
package org.v8LogScanner.rgx;

import com.sun.net.httpserver.Headers;
import org.v8LogScanner.commonly.Filter;
import org.v8LogScanner.commonly.Filter.ComparisonTypes;
import org.v8LogScanner.rgx.RegExp.PropTypes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class RegExp implements Serializable {

    private static final long serialVersionUID = 5068481599783731039L;

    public enum PropTypes {
        Time, Duration, Event, StackLevel, Content, Process,
        ClientID, Except, Descr, Protected, CallID, ProcessName, ANY, ComputerName,
        ApplicationName, ConnectID, SessionID, Usr, WaitConnections, Locks, Regions,
        Context, DeadlockConnectionIntersections, Interface, Sql, Trans, Dbpid, Sdbl,
        Func, Txt, UsrLim, Type, Method, URI, Headers, Body, Status, Phrase, FileName, planSQLText
    }

    public enum EventTypes {ANY, EXCP, CONN, TLOCK, TDEADLOCK, TTIMEOUT, DBMSSQL, SDBL, DBV8DBEng, DBORACLE, DBPOSTGRS, HASP, VRSREQUEST, VRSRESPONSE}

    private EventTypes eventType;
    private final RgxNode rgxNode = new RgxNode();

    public RegExp(EventTypes et) {

        eventType = et;

        rgxNode.add(PropTypes.Time);
        rgxNode.add(PropTypes.Duration);
        rgxNode.add(PropTypes.Event);
        rgxNode.add(PropTypes.StackLevel);
        rgxNode.add(PropTypes.Content);
        try {
            Class<?> c = Class.forName(this.getClass().getName());
            Method  method = c.getDeclaredMethod ("build" + et.toString());
            method.invoke (this, new Object[0]);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        /*
        switch (et) {
            case EXCP:
                buildEXCP();
                break;
            case CONN:
                buildCONN();
                break;
            case TLOCK:
                buildTLOCK();
                break;
            case TDEADLOCK:
                buildTDEADLOCK();
                break;
            case TTIMEOUT:
                buildTTIMEOUT();
                break;
            case DBMSSQL:
                buildDBMSSQL();
                break;
            case ANY:
                buildANY();
                break;
            case SDBL:
                buildSDBL();
                break;
            case DBV8DBEng:
                buildDBV8DBEng();
                break;
            case DBORACLE:
                buildDBORACLE();
                break;
            case DBPOSTGRS:
                buildDBPOSTGRS();
                break;
            case HASP:
                buildHASP();
                break;
            case VRSREQUEST:
                buildVRSREQUEST();
                break;
            case VRSRESPONSE:
                buildVRSRESPONSE();
                break;
            default:
                break;

        }
        */
    }

    public RegExp() {
        this(EventTypes.ANY);
    }

    // Exceptions that are not handled with normal cases
    private void buildEXCP() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Except);
        rgxNode.add(PropTypes.Descr);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("EXCP");
    }

    // Establishing or disconnecting client connections from server
    private void buildCONN() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.Protected);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("CONN");
    }

    // Attempt to acquire 1c lock
    private void buildTLOCK() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Regions);
        rgxNode.add(PropTypes.Locks);
        rgxNode.add(PropTypes.WaitConnections);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("TLOCK");
    }

    private void buildTDEADLOCK() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.DeadlockConnectionIntersections);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("TDEADLOCK");
    }

    private void buildTTIMEOUT() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.WaitConnections);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("TTIMEOUT");
    }

    private void buildANY() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.Interface);
        rgxNode.add(PropTypes.CallID);
        //rgxNode.add(PropTypes.Descr);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("");
    }

    private void buildDBMSSQL() {
        buildSQlEvent();

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("DBMSSQL");
    }

    private void buildDBORACLE() {
        buildSQlEvent();

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("DBORACLE");
    }

    private void buildDBPOSTGRS() {
        buildSQlEvent();

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("DBPOSTGRS");
    }

    private void buildSQlEvent() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Trans);
        rgxNode.add(PropTypes.Dbpid);
        rgxNode.add(PropTypes.Sql);
        rgxNode.add(PropTypes.planSQLText);
        rgxNode.add(PropTypes.Context);
    }

    private void buildSDBL() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Trans);
        rgxNode.add(PropTypes.Sdbl);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("SDBL");
    }

    private void buildDBV8DBEng() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Trans);
        rgxNode.add(PropTypes.Sql);
        rgxNode.add(PropTypes.Func);
        rgxNode.add(PropTypes.FileName);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("DBV8DBEng");
    }

    private void buildHASP() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.Txt);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.UsrLim);
        rgxNode.add(PropTypes.Type);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("HASP");
    }

    private void buildVRSREQUEST() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.Method);
        rgxNode.add(PropTypes.URI);
        rgxNode.add(PropTypes.Headers);
        rgxNode.add(PropTypes.Body);
        rgxNode.add(PropTypes.Context);

        Filter<String> filter = getFilter(PropTypes.Event);
        filter.add("VRSREQUEST");
    }

    private void buildVRSRESPONSE() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.Status);
        rgxNode.add(PropTypes.Phrase);
        rgxNode.add(PropTypes.Headers);
        rgxNode.add(PropTypes.Body);
        rgxNode.add(PropTypes.Context);
    }

    /**
     * Compile rgxNodes expression starting at propStart (inclusive)
     * and finishing at porpEnd (inclusive). ANY prop
     * gets all properties with active filter except last Content prop.
     **/
    public Pattern compileSpan(PropTypes propStart, PropTypes propEnd) {

        //if (useCache){
        //  Pattern curr = cache.get("compiledPattern");
        //  if (curr != null)
        //    return curr;
        //}

        Pattern result = Pattern.compile(rgxNode.interpretSpan(propStart, propEnd), Pattern.DOTALL);
        //cache.putVal("compiledPattern", result);
        return result;
    }

    public List<PropTypes> getGroupingProps() {
        return rgxNode.grouping_props;
    }

    public Filter<String> getFilter(PropTypes key) {
        return getProp(key).getFilter();
    }

    /**
     * Construct new array of a Filter elements and return it.
     * Note that changing filter's array elements is not affected the actual filtering of a RgxNode
     *
     * @return map consisting of a copy RgxNode actual filters
     */
    public Map<PropTypes, Filter<String>> getFilters() {
        Map<PropTypes, Filter<String>> result = new HashMap<>();
        for (RgxNode el : rgxNode.getElements()) {
            if (el.getFilter().isActive()) {
                result.merge(el.getType(), el.getFilter(),
                        (val1, val2) -> {
                            val1.getElements().addAll(val2.getElements());
                            return val1;
                        });
            }
        }
        return result;
    }

    public void setFilters(Map<PropTypes, Filter<String>> filters) {
        Set<PropTypes> filterProps = filters.keySet();
        for (PropTypes uProp : filterProps) {
            try {
                Filter<String> filter = getFilter(uProp);
                filter.reset();
                filter.getElements().addAll(filters.get(uProp).getElements());
            } catch (PropertyNotFoundException e) {
                // Do nothing, user made a mistake with
                // choosing prop for this regExp type
            }
        }
    }

    public ArrayList<String> getUnicRgxPropText() {

        ArrayList<String> propsText = new ArrayList<>();

        for (int i = 0; i < rgxNode.getElements().size(); i++) {
            RgxNode curr = rgxNode.getElements().get(i);

            if (!curr.isUnique())
                continue;

            propsText.add(getThisPropText(curr.getType()));
        }
        return propsText;
    }

    public ArrayList<String> getPredecessorPropText(PropTypes key) {

        ArrayList<String> propsText = new ArrayList<>();

        if (key != PropTypes.Event)
            return propsText;

        LinkedList<RgxNode> props = rgxNode.getElements();
        for (RgxNode prop : props) {

            if (prop.pType == key)
                break;
            propsText.add(getThisPropText(prop.getType()));
        }

        return propsText;
    }

    public String getThisPropText(PropTypes key) {

        String textProp = "";

        Filter<String> filter = getFilter(key);
        if (!filter.isActive()) {
            filter.add("");
            textProp = getProp(key).interpret();
            filter.reset();
        } else
            textProp = getProp(key).interpret();

        return textProp;
    }

    private RgxNode getProp(PropTypes prop) throws PropertyNotFoundException {
        if (!rgxNode.getIndexator().containsKey(prop))
            throw new PropertyNotFoundException();
        return rgxNode.getElements().get(rgxNode.getIndexator().get(prop));
    }

    public EventTypes getEventType() {
        return eventType;
    }

    public boolean filterActive() {

        // Event PropTypes.Event was setting by default during instantiation RegExp class
        for (RgxNode _node : rgxNode.getElements()) {
            if (_node.pType != PropTypes.Event && _node.getFilter().isActive()) {
                return true;
            }
        }
        return false;
    }

    public ConcurrentMap<PropTypes, List<String>> getIntegerFilters() {

        ConcurrentMap<PropTypes, List<String>> filterMap = new ConcurrentHashMap<>();
        // Event PropTypes.Event was setting by default during instantiation RegExp class
        for (RgxNode _node : rgxNode.getElements()) {
            if (_node.pType != PropTypes.Event
                    && _node.getFilter().isActive()
                    && (_node.pType == PropTypes.Time || _node.pType == PropTypes.Duration)
                    ) {
                List<String> filterVals = Collections.synchronizedList(new ArrayList<String>());
                _node.getFilter().forEach(n -> filterVals.add(n));
                filterMap.put(_node.pType, filterVals);
            }
        }
        return filterMap;
    }

    public ConcurrentMap<PropTypes, ComparisonTypes> getIntegerCompTypes() {

        ConcurrentMap<PropTypes, ComparisonTypes> filterMap = new ConcurrentHashMap<>();
        // Event PropTypes.Event was setting by default during instantiation RegExp class
        for (RgxNode _node : rgxNode.getElements()) {
            if (_node.pType != PropTypes.Event
                    && _node.getFilter().isActive()
                    && isNumericProp(_node.pType)
                    ) {

                filterMap.put(_node.pType, _node.getFilter().getComparisonType());
            }
        }
        return filterMap;
    }

    public static boolean isNumericProp(PropTypes prop) {
        if (prop == PropTypes.Time
                || prop == PropTypes.Duration)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return eventType.toString();
    }

    public ArrayList<PropTypes> getPropsForGrouping() {

        ArrayList<PropTypes> props = new ArrayList<>();
        for (RgxNode el : rgxNode.getElements()) {
            if (el.getType() == PropTypes.Content)
                continue;
            props.add(el.getType());
        }

        return props;
    }

    public ArrayList<PropTypes> getPropsForFiltering() {

        ArrayList<PropTypes> props = new ArrayList<>();
        for (RgxNode el : rgxNode.getElements()) {
            if (el.getType() == PropTypes.Event || el.getType() == PropTypes.Content)
                continue;
            props.add(el.getType());
        }

        return props;
    }

}

class PropertyNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -5042838545940786464L;

    public PropertyNotFoundException() {
        super("Property doesn't exist in this rgx event!");
    }
}

////////////////////////////////////////////////////////////
// NODES
////////////////////////////////////////////////////////////
class RgxNode implements Cloneable, Serializable {

    private static final long serialVersionUID = -4628782283441175339L;

    protected Filter<String> filter = new StrokeFilter();
    final List<PropTypes> grouping_props = new LinkedList<>();
    private LinkedList<RgxNode> elements = new LinkedList<RgxNode>();
    private HashMap<PropTypes, Integer> indexator = new HashMap<>();
    protected PropTypes pType = null;
    public final String name = null;

    public RgxNode() {
    }

    public boolean isUnique() {
        // List of properties deleting from single event block for grouping purposes
        if (this instanceof Time
                || this instanceof Duration
                || this instanceof StackLevel
                || this instanceof ClientID
                || this instanceof CallID
                || this instanceof Interface)

            return true;

        return false;

    }

    // FACTORY METHOD PATTERN
    public void add(PropTypes key) {
        RgxNode el = null;
       /* try {
            Class<?> c = Class.forName(this.getClass().getName() + "." + key.toString());
            el = (RgxNode) c.newInstance();
        } catch (InstantiationException  | ClassNotFoundException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        */

        switch (key) {
            case Time:
                el = new Time();
                break;
            case ClientID:
                el = new ClientID();
                break;
            case Content:
                el = new Content();
                break;
            case Descr:
                el = new Descr();
                break;
            case Duration:
                el = new Duration();
                break;
            case Event:
                el = new Event();
                break;
            case Except:
                el = new Except();
                break;
            case Process:
                el = new Process();
                break;
            case ProcessName:
                el = new Process();
                break;
            case Status:
                el = new Status();
                break;
            case Phrase:
                el = new Phrase();
                break;
            case Method:
                el = new Method();
                break;
            case URI:
                el = new URI();
                break;
            case Headers:
                el = new Headers();
                break;
            case Body:
                el = new Body();
                break;
            case StackLevel:
                el = new StackLevel();
                break;
            case FileName:
                el = new FileName();
                break;
            case Protected:
                el = new Protected();
                break;
            case CallID:
                el = new CallID();
                break;
            case ComputerName:
                el = new ComputerName();
                break;
            case ApplicationName:
                el = new ApplicationName();
                break;
            case ConnectID:
                el = new ConnectID();
                break;
            case SessionID:
                el = new SessionID();
                break;
            case Usr:
                el = new Usr();
                break;
            case Regions:
                el = new Regions();
                break;
            case Locks:
                el = new Locks();
                break;
            case WaitConnections:
                el = new WaitConnections();
                break;
            case Context:
                el = new Context();
                break;
            case DeadlockConnectionIntersections:
                el = new DeadlockConnectionIntersections();
                break;
            case Interface:
                el = new Interface();
                break;
            case Sql:
                el = new Sql();
                break;
            case Trans:
                el = new Trans();
                break;
            case Dbpid:
                el = new Dbpid();
                break;
            case Sdbl:
                el = new Sdbl();
                break;
            case Func:
                el = new Func();
                break;
            case planSQLText:
                el = new PlanSQLText();
                break;
            case Txt:
                el = new Txt();
                break;
            case Type:
                el = new Type();
                break;
            case UsrLim:
                el = new UsrLim();
                break;
            default:
                break;
        }


        el.pType = key;

        elements.add(el);
        indexator.put(key, elements.indexOf(el));

    }

    public Filter<String> getFilter() {
        return filter;
    }

    public String interpret() {

        String result = "";
        for (int i = 0; i < elements.size(); i++) {
            result = result.concat(intepretElement(i));
        }
        result = result.replaceFirst(",$", "");

        if (!result.matches(".*(\\.\\*)$"))
            result = result.concat(".*");
        return result;
    }

    public String interpretSpan(PropTypes startProp, PropTypes endProp) {

        String result = "";

        int iStart = 0;
        int iEnd = elements.size() - 1;

        if (startProp != PropTypes.ANY)
            iStart = indexator.get(startProp);

        if (endProp != PropTypes.ANY)
            iEnd = indexator.get(endProp);

        for (int i = iStart; i <= iEnd; i++)
            result = result.concat(intepretElement(i));

        return result;
    }

    private String intepretElement(int index) {

        String result = "";

        RgxNode node = elements.get(index);

        String currExp = node.interpret();

        result += currExp;

        if (node.getFilter().isActive()
                && !(node instanceof Content)
                && !(node instanceof Event))
            result += ".*";

        return result;
    }

    /**
     * Strict i.e. seek prop values in input event that completely equally a whole filter value
     *
     * @param resVal - initial name of event property
     * @return String - name property with its inner text
     */
    protected String compStrFilter(String resVal) {
        String result = "";

        if (filter.isActive()) {
            for (String el : filter) {
                result = result.concat(result.isEmpty() ? el : "|" + el);
            }
            if (result.isEmpty())
                return resVal + "[^,]*";
            result = filter.size() > 1 ? "(" + result + ")" : result;
            result = resVal + result;
        } else
            result = "";
        return result;

    }

    /**
     * //Not strict i.e. allow matching part of prop value according to the filter value
     *
     * @param resVal - initial name of event property
     * @return String - name property with its inner text
     */
    public String compPartStrFilter(String resVal) {

        String result = "";
        if (filter.isActive()) {
            for (String el : filter) {
                result = result.concat(result.isEmpty() ? el : "|" + el);
            }

            if (result.isEmpty())
                return resVal + "[^,]*";

            result = filter.size() > 1 ? "(" + result + ")" : result;

            result = resVal + ".*" + result + ".*";
        } else
            result = "";
        return result;
    }

    public LinkedList<RgxNode> getElements() {
        return elements;
    }

    public PropTypes getType() {
        return pType;
    }

    public HashMap<PropTypes, Integer> getIndexator() {
        return indexator;
    }

    @Override
    public String toString() {
        return pType.toString();
    }

    // overloaded for integer Nodes as Time, Duration etc
    public boolean filterIntegers(String input) {
        return true;
    }

    class Time extends RgxNode {

        private static final long serialVersionUID = 6890230705651035556L;
        public final String name = "\\d{2}:\\d{2}\\.\\d+-";

        public Time() {
            this.filter = new TimeFilter();
        }

        @Override
        public String interpret() {
            return name;
        }
    }

    // Time is stored in the duration property in microseconds since 8.3. 1 mSec = 1e -6 sec
    class Duration extends RgxNode {

        private static final long serialVersionUID = 5870526855715479805L;
        public final String name = "\\d+";

        public String interpret() {
            return name + ",";
        }
    }

    class Event extends RgxNode {

        private static final long serialVersionUID = 7934235031850178870L;
        public final String name = "\\w+";

        @Override
        public String interpret() {

            String result = "";
            if (filter.isActive()) {
                for (String el : filter) {
                    result = result.concat(result.isEmpty() ? el : "|" + el);
                }
                if (result.isEmpty())
                    result = result.concat(name);
                result = filter.size() > 1 ? "(" + result + ")" : result;
                result += ",";
            } else
                result = "";
            return result;

      /*
      String result = "";
      if (filter.isActive()){
        for (String el : filter){
          result += (result.isEmpty())? el : "|"+el;
        }
        if (result.isEmpty())
          return "" + "[^,]+";
        result = filter.size()>1 ? "("+result+")" : result;
        result = "" + ".*"+result+".*";
      }
      else
        result = name;
      return result;
    */
        }
    }

    class StackLevel extends RgxNode {

        private static final long serialVersionUID = -8185161751596301657L;
        public final String name = "\\d";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class Process extends RgxNode {

        private static final long serialVersionUID = 9172044611574461835L;
        public final String name = "process=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // Name of base1c (or name of server context)
    class ProcessName extends RgxNode {

        private static final long serialVersionUID = -2364638723964896685L;
        public final String name = "p:processName=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class ClientID extends RgxNode {

        private static final long serialVersionUID = -619884946892311224L;
        public final String name = "(ClientID=|t:clientID=)";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Except extends RgxNode {

        private static final long serialVersionUID = 1650120525969706095L;
        public final String name = "Exception=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Descr extends RgxNode {

        private static final long serialVersionUID = -7708565547388402657L;
        public final String name = "Descr=";

        @Override
        public String interpret() {

            return compPartStrFilter(name);
        }
    }

    class Protected extends RgxNode {

        private static final long serialVersionUID = 8391926316570786681L;
        public final String name = "Protected=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // fake property, means remaining event string
    class Content extends RgxNode {

        private static final long serialVersionUID = -1541157702313342752L;
        public final String name = "";

        @Override
        public String interpret() {

            String result = "";
            if (filter.isActive()) {
                for (String el : filter) {
                    result += (result.isEmpty()) ? el : "|" + el;
                }
                result = filter.size() > 1 ? "(" + result + ")" : result;
                result = ".*" + result + ".*";
            } else
                result = ".*";
            return result;
        }
    }

    // absent in a documentation
    class CallID extends RgxNode {

        private static final long serialVersionUID = 9149186189379735123L;
        public final String name = "CallID=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // ID client application (for example: 1CV8C)
    class ApplicationName extends RgxNode {

        private static final long serialVersionUID = 2674892819877737727L;
        public final String name = "t:applicationName";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // name of working server
    class ComputerName extends RgxNode {

        private static final long serialVersionUID = 1015096494934066642L;
        public final String name = "t:computerName=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // connect id with 1c database
    class ConnectID extends RgxNode {

        private static final long serialVersionUID = 8964648848508335156L;
        public final String name = "t:connectID=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // ID connection with sql base (SPID)
    class SessionID extends RgxNode {

        private static final long serialVersionUID = 4574192688144363561L;
        public final String name = "SessionID=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // user name of 1c database (DefUser if no users exist)
    class Usr extends RgxNode {

        private static final long serialVersionUID = 3565536406246751380L;
        public final String name = "Usr=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    // Region name occupied by 1c locks
    class Regions extends RgxNode {

        private static final long serialVersionUID = -4417480107697416087L;
        public final String name = "Regions=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Locks extends RgxNode {

        private static final long serialVersionUID = 4877103213805561452L;
        public final String name = "Locks=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class WaitConnections extends RgxNode {

        private static final long serialVersionUID = 5305961518303696210L;
        public final String name = "WaitConnections=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Context extends RgxNode {

        private static final long serialVersionUID = 4105776193808554124L;
        public final String name = "Context=";

        @Override
        public String interpret() {

            return compPartStrFilter(name);
        }
    }

    class DeadlockConnectionIntersections extends RgxNode {

        private static final long serialVersionUID = -2222855887771818713L;
        public final String name = "DeadlockConnectionIntersections=";

        @Override
        public String interpret() {
            return compPartStrFilter(name);
        }
    }

    class Interface extends RgxNode {

        private static final long serialVersionUID = 3565023182977645438L;
        public final String name = "Interface=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Sql extends RgxNode {

        private static final long serialVersionUID = 8038487573208647649L;
        public final String name = "Sql=";

        @Override
        public String interpret() {

            return compPartStrFilter(name);
        }
    }

    class Trans extends RgxNode {

        private static final long serialVersionUID = -3050197160990759709L;
        public final String name = "Trans=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Dbpid extends RgxNode {

        private static final long serialVersionUID = -7070580971011620134L;
        public final String name = "dbpid=";

        @Override
        public String interpret() {

            return compStrFilter(name);
        }
    }

    class Sdbl extends RgxNode {

        private static final long serialVersionUID = -8513195093613655268L;
        public final String name = "Sdbl=";

        public String interpret() {

            return compPartStrFilter(name);
        }
    }

    class Func extends RgxNode {

        private static final long serialVersionUID = -7918919322454464280L;
        public final String name = "Func=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class Txt extends RgxNode {

        private static final long serialVersionUID = -7943899322454464280L;
        public final String name = "Txt=";

        @Override
        public String interpret() {
            return compPartStrFilter(name);
        }
    }

    class UsrLim extends RgxNode {

        private static final long serialVersionUID = -7918919722454464280L;
        public final String name = "UsrLim=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class Type extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "type=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class PlanSQLText extends RgxNode {

        private static final long serialVersionUID = -7218919322454464280L;
        public final String name = "planSQLText=";

        @Override
        public String interpret() {
            return compPartStrFilter(name);
        }
    }

    class Method extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "Method=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class URI extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "URI=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class Body extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "Body=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class Status extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "Status=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class Phrase extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "Phrase=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }

    class FileName extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "FileName=";

        @Override
        public String interpret() {
            return compStrFilter(name);
        }
    }


    class Headers extends RgxNode {

        private static final long serialVersionUID = -7918911322454464280L;
        public final String name = "Headers=";

        @Override
        public String interpret() {
            return compPartStrFilter(name);
        }
    }

}