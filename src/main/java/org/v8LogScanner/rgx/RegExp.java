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
        ANY, Time, Duration, Event, StackLevel, Content, Process,
        ClientID, Except, Descr, Protected, CallID, ProcessName, ComputerName,
        ApplicationName, ConnectID, SessionID, Usr, WaitConnections, Locks, Regions,
        Context, DeadlockConnectionIntersections, Interface, Sql, Trans, Dbpid, Sdbl,
        Func, Txt, UsrLim, Type, Method, URI, Headers, Body, Status, Phrase, FileName, PlanSQLText
    }

    public enum EventTypes {ANY, CONN, DBMSSQL, DBV8DBEng, DBORACLE, DBPOSTGRS, EXCP, HASP, LEAKS, MEM, QERR, SDBL,  TLOCK, TDEADLOCK, TTIMEOUT, VRSREQUEST, VRSRESPONSE, SCALL, CALL}

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
            Class<?> c = Class.forName(RegExp.class.getName());
            Method  method = c.getDeclaredMethod ("build" + et.toString());
            method.invoke (this, new Object[0]);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
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

        Filter filter = getFilter(PropTypes.Event);
        filter.add("EXCP");
    }

    // Establishing or disconnecting client connections from server
    private void buildCONN() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.Protected);

        Filter filter = getFilter(PropTypes.Event);
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

        Filter filter = getFilter(PropTypes.Event);
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

        Filter filter = getFilter(PropTypes.Event);
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

        Filter filter = getFilter(PropTypes.Event);
        filter.add("TTIMEOUT");
    }

    private void buildANY() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.Interface);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.CallID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Descr);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("");
    }

    private void buildSCALL() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.Interface);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.CallID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Descr);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("SCALL");
    }

    private void buildCALL() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.Interface);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.CallID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Descr);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("CALL");
    }

    private void buildDBMSSQL() {
        buildSQlEvent();

        Filter filter = getFilter(PropTypes.Event);
        filter.add("DBMSSQL");
    }

    private void buildDBORACLE() {
        buildSQlEvent();

        Filter filter = getFilter(PropTypes.Event);
        filter.add("DBORACLE");
    }

    private void buildDBPOSTGRS() {
        buildSQlEvent();

        Filter filter = getFilter(PropTypes.Event);
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
        rgxNode.add(PropTypes.PlanSQLText);
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

        Filter filter = getFilter(PropTypes.Event);
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

        Filter filter = getFilter(PropTypes.Event);
        filter.add("DBV8DBEng");
    }

    private void buildHASP() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.Txt);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.UsrLim);
        rgxNode.add(PropTypes.Type);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("HASP");
    }

    private void buildVRSREQUEST() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.Method);
        rgxNode.add(PropTypes.URI);
        rgxNode.add(PropTypes.Headers);
        rgxNode.add(PropTypes.Body);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("VRSREQUEST");
    }

    private void buildVRSRESPONSE() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.Status);
        rgxNode.add(PropTypes.Phrase);
        rgxNode.add(PropTypes.Headers);
        rgxNode.add(PropTypes.Body);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("VRSRESPONSE");
    }

    private void buildQERR() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ConnectID);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Descr);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("QERR");
    }

    private void buildLEAKS() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Descr);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("LEAKS");
    }

    private void buildMEM() {
        rgxNode.add(PropTypes.Process);
        rgxNode.add(PropTypes.ProcessName);
        rgxNode.add(PropTypes.ClientID);
        rgxNode.add(PropTypes.ApplicationName);
        rgxNode.add(PropTypes.ComputerName);
        rgxNode.add(PropTypes.SessionID);
        rgxNode.add(PropTypes.Usr);
        rgxNode.add(PropTypes.Descr);
        rgxNode.add(PropTypes.Context);

        Filter filter = getFilter(PropTypes.Event);
        filter.add("MEM");
    }

    /**
     * Compile rgxNodes expression starting at propStart (inclusive)
     * and finishing at porpEnd (inclusive). ANY prop
     * gets all properties with active filter except last Content prop.
     **/
    public Pattern compileSpan(PropTypes propStart, PropTypes propEnd) {
        Pattern result = Pattern.compile(rgxNode.interpretSpan(propStart, propEnd), Pattern.DOTALL);
        return result;
    }

    public List<PropTypes> getGroupingProps() {
        return rgxNode.grouping_props;
    }

    public Filter getFilter(PropTypes key) {
        return getProp(key).getFilter();
    }

    /**
     * Construct new array of a Filter elements and return it.
     * Note that changing filter's array elements is not affected the actual filtering of a RgxNode
     *
     * @return map consisting of a copy RgxNode actual filters
     */
    public Map<PropTypes, Filter> getFilters() {
        Map<PropTypes, Filter> result = new HashMap<>();
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

    public void setFilters(Map<PropTypes, Filter> filters) {
        Set<PropTypes> filterProps = filters.keySet();
        for (PropTypes uProp : filterProps) {
            try {
                Filter filter = getFilter(uProp);
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

        Filter filter = getFilter(key);
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
