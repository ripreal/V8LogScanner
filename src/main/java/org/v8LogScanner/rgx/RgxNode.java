package org.v8LogScanner.rgx;


import org.v8LogScanner.commonly.Filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

////////////////////////////////////////////////////////////
// NODES
////////////////////////////////////////////////////////////
class RgxNode implements Cloneable, Serializable {

    private static final long serialVersionUID = -4628782283441175339L;

    protected Filter<String> filter = new StrokeFilter();
    final List<RegExp.PropTypes> grouping_props = new LinkedList<>();
    private LinkedList<RgxNode> elements = new LinkedList<RgxNode>();
    private HashMap<RegExp.PropTypes, Integer> indexator = new HashMap<>();
    protected RegExp.PropTypes pType = null;
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
    public void add(RegExp.PropTypes key) {
        RgxNode el = null;

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
                el = new ProcessName();
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

    public String interpretSpan(RegExp.PropTypes startProp, RegExp.PropTypes endProp) {

        String result = "";

        int iStart = 0;
        int iEnd = elements.size() - 1;

        if (startProp != RegExp.PropTypes.ANY)
            iStart = indexator.get(startProp);

        if (endProp != RegExp.PropTypes.ANY)
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

    public RegExp.PropTypes getType() {
        return pType;
    }

    public HashMap<RegExp.PropTypes, Integer> getIndexator() {
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