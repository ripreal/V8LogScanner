package org.v8LogScanner.logsCfg;

import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.rgx.RegExp;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogBuilder {

    private List<LogEvent> events = new ArrayList<>();
    private List<LogLocation> locations = new ArrayList<>();
    private List<LogProperty> properties = new ArrayList<>();
    private LogDump dumps = new LogDump();
    private List<String> cfgPaths = null;
    private boolean allowPLanSql = false;

    public LogBuilder() {
        cfgPaths = LogConfig.cfgPaths();
    }

    public LogBuilder(List<String> cfgPaths) {
        this.cfgPaths = cfgPaths;
    }

    public File writeToXmlFile() {
        File logFile = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            Document doc = docFactory.newDocumentBuilder().newDocument();

            // root elements
            Element root = doc.createElement(LogConfig.CONFIG_TAG_NAME);
            root.setAttribute(LogConfig.CONFIG_PROP_NAME, LogConfig.CONFIG_PROP_VAL);
            doc.appendChild(root);

            Element dumpEl = doc.createElement(LogConfig.DUMP_TAG_NAME);
            dumpEl.setAttribute(LogConfig.CREATE_NAME, dumps.getCreate());
            root.appendChild(dumpEl);

            Element logEl = doc.createElement(LogConfig.LOC_TAG_NAME);
            for(LogLocation loc : locations) {

                logEl.setAttribute(LogConfig.LOC_HISTORY_NAME, loc.getHistory());
                logEl.setAttribute(LogConfig.LOC_LOCATION_NAME, loc.getLocation());

                events.forEach((event) -> {
                    Element eventEl = doc.createElement(LogConfig.EVENT_TAG_NAME);
                    for(LogEvent.EventRow eventRow : event) {
                        Element eventPropEl = doc.createElement(eventRow.getComparison());
                        eventPropEl.setAttribute(LogConfig.PROP_NAME, eventRow.getKey());
                        eventPropEl.setAttribute(LogConfig.VAL_NAME, eventRow.getVal());
                        eventEl.appendChild(eventPropEl);
                    };
                    logEl.appendChild(eventEl);
                });

                properties.forEach((prop) -> {
                    Element propEl = doc.createElement(LogConfig.PROP_NAME);
                    propEl.setAttribute(LogConfig.PROPERTY_PROP_NAME, prop.getName());

                    Element dummyEl = doc.createElement("dummy");
                    propEl.appendChild(dummyEl);

                    logEl.appendChild(propEl);
                });

                root.appendChild(logEl);
            }

            if (allowPLanSql) {
                Element planEl = doc.createElement(LogConfig.PLAN__SQL_TAG_NAME);
                root.appendChild(planEl);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            for(String path : cfgPaths) {
                logFile = new File(path);
                StreamResult streamresult = new StreamResult(logFile);
                transformer.transform(source, streamresult);

                //StreamResult streamresult_test = new StreamResult(System.out);
                //transformer.transform(source, streamresult_test);
                removeNodeElement(logFile, ".*dummy.*");
            };
        } catch (Exception e) {
            e.printStackTrace();
            // no need to handle this
        }
        return logFile;
    }

    public LogBuilder readCfgFile(String path) throws RuntimeException {

        events.clear();
        locations.clear();
        properties.clear();
        dumps = new LogDump();

        File logFile = new File(path);

        if (!logFile.exists())
            throw new RuntimeException("file does not exists!");

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            Document doc = docFactory.newDocumentBuilder().parse(logFile);

            if (!doc.getDocumentElement().getTagName().matches(LogConfig.CONFIG_TAG_NAME))
                throw new RuntimeException(String.format(
                    "Wrong file format. Root element must start with <%s> tag",LogConfig.CONFIG_TAG_NAME));

            parseLogCfg(doc.getFirstChild().getChildNodes());

        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e.getStackTrace().toString());
        }
        return this;
    }

    /**
     * Contruct log file for analyzing all events logged by 1c
     * @return prepared but not written text file with settings
     */
    public File buildAllEvents() {
        return this
        .addEvent(LogEvent.LogEventComparisons.ne, RegExp.PropTypes.Event, "")
        .addLogProperty("all")
        .writeToXmlFile();
    }

    /**
     * Construct log file for analyzing dbmsql, dbpostgrs, db2, dboracle database events.
     * To analyse deadlocks and other sql errors additional excp event is added.
     * Note you need to add the location tag in on order to get *.log file working.
     * @return prepared but not written text file with settings.
     */
    public File buildSQlEvents() {
        return this
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "dbmssql")
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "dbpostgrs")
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "db2")
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "dboracle")
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "excp")
            .addLogProperty("all")
            .writeToXmlFile();
    }

    /**
     * Construct the *.log file for analyzing excp events
     * Note you need to add the location tag in on order to get *.log file working.
     * @return prepared but not written text file with settings
     */
    public File buildExcpEvents() {
        return this
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "excp")
            .addLogProperty("all")
            .writeToXmlFile();
    }

    /**
     * Construct the *.log file for analyzing long any events
     * Note you need to add the location tag in on order to get *.log file working.
     * @return prepared but not written text file with settings
     */
    public File buildLongEvents() {

        LogEvent longEv = new LogEvent();

        longEv.setProp(RegExp.PropTypes.Event);
        longEv.setVal(RegExp.PropTypes.Event, "");
        longEv.setComparison(RegExp.PropTypes.Event, LogEvent.LogEventComparisons.ne);

        longEv.setProp(RegExp.PropTypes.Duration);
        longEv.setVal(RegExp.PropTypes.Duration, "20000000");
        longEv.setComparison(RegExp.PropTypes.Duration, LogEvent.LogEventComparisons.ge);

        return this
            .addEvent(longEv)
            .addLogProperty("all")
            .writeToXmlFile();
    }

    /**
     * Construct the *.log file for analyzing managed locks put by 1c Enterprise
     * Note you need to add the location tag in on order to get *.log file working.
     * @return prepared but not written text file with settings
     */
    public File build1cLocksEvents(){
        return this
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, RegExp.EventTypes.TLOCK.toString())
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, RegExp.EventTypes.TTIMEOUT.toString())
            .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, RegExp.EventTypes.TDEADLOCK.toString())
            .addLogProperty("all")
            .writeToXmlFile();
    }

    /**
     * Construct the *.log file for analyzing all DB MSQL database queries with the filter by user and infobase.
     * Note sql plan also included.
     * Note you need to add the location tag in on order to get *.log file working.
     * @param user - user descr field in 1c enterpise
     * @param infobase_name name of base in conection string
     * @return prepared but not written text file with settings
     */
    public File buildInvestigateNonEffectiveQueries(String user, String infobase_name){

        LogEvent event = new LogEvent();

        event.setProp(RegExp.PropTypes.Event);
        event.setVal(RegExp.PropTypes.Event, RegExp.EventTypes.DBMSSQL.toString());
        event.setComparison(RegExp.PropTypes.Event, LogEvent.LogEventComparisons.eg);

        event.setProp(RegExp.PropTypes.ProcessName);
        event.setVal(RegExp.PropTypes.ProcessName, "%" + infobase_name + "%");
        event.setComparison(RegExp.PropTypes.ProcessName, LogEvent.LogEventComparisons.like);

        event.setProp(RegExp.PropTypes.Usr);
        event.setVal(RegExp.PropTypes.Usr, user);
        event.setComparison(RegExp.PropTypes.Usr, LogEvent.LogEventComparisons.eg);

        return this
            .addEvent(event)
            .addLogProperty("all")
            .setPlanSql(true)
            .writeToXmlFile();
    }

    /**
     * Construct the *.log file for analyzing excp, conn, proc, admin, sesn, clstr events.
     * Note you need to add the location tag in on order to get *.log file working.
     * @return prepared but not written text file with settings
     */
    public File buildEverydayEvents() {
        return this
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "excp")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "conn")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "PROC")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "ADMIN")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "SESN")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "CLSTR")
        .addLogProperty("all")
        .writeToXmlFile();
    }

    public LogBuilder addLocLocation(String location, String history) {
        locations.add(new LogLocation(location, history));
        return this;
    }

    public LogBuilder addLocLocation() {
        locations.add(new LogLocation());
        return this;
    }

    public LogBuilder addEvent(LogEvent.LogEventComparisons comparison, RegExp.PropTypes event, String  val) {
        events.add(new LogEvent(comparison, event, val));
        return this;
    }

    public LogBuilder addEvent(LogEvent event) {
        events.add(event);
        return this;
    }

    public LogBuilder addEvent() {
        events.add(new LogEvent());
        return this;
    }

    public LogBuilder addLogProperty(String name) {
        properties.add(new LogProperty(name));
        return this;
    }

    public LogBuilder setCreateDumps(boolean create) {
        dumps.setCreate(create);
        return this;
    }

    public LogBuilder setCreateDumps(String create) {
        setCreateDumps(Boolean.parseBoolean(create));
        return this;
    }

    public LogBuilder addLogProperty() {
        properties.add(new LogProperty());
        return this;
    }

    public List<LogEvent> getLogEvents() {
        return events;
    }

    public LogBuilder setPlanSql(boolean allow) {
        allowPLanSql = allow;
       return this;
    }

    public List<String> getCfgPaths() {
        return cfgPaths;
    }

    public List<String> getLocations() {
        List<String> res = new ArrayList<>();
        locations.forEach((item) -> res.add(item.getLocation()));
        return res;
    }

    public void clearLocations() {
        locations.clear();
    }

    private void removeNodeElement(File logFile, String pattern) throws IOException {

        List<String> strings = fsys.readAllFromFileToList(logFile.getAbsolutePath());

        List<String> processedStrings =
        strings
            .stream()
            .filter((el) -> !el.matches(pattern))
            .collect(Collectors.toList());

        fsys.writeInNewFile(processedStrings, logFile);
    }

    private void parseLogCfg(NodeList nodeList) {

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            String nodeName = node.getNodeName();
            if (nodeName.matches("(?i)" + LogConfig.DUMP_TAG_NAME)) {
                setCreateDumps(node.getAttributes().getNamedItem(LogConfig.CREATE_NAME).getNodeValue());
            } else if (nodeName.matches("(?i)" + LogConfig.LOC_TAG_NAME)) {
                NamedNodeMap attributes = node.getAttributes();
                addLocLocation(
                        attributes.getNamedItem(LogConfig.LOC_LOCATION_NAME).getNodeValue().toString(),
                        attributes.getNamedItem(LogConfig.LOC_HISTORY_NAME).getNodeValue().toString()
                );
                parseLogCfg(node.getChildNodes());
            } else if (nodeName.matches("(?i)" + LogConfig.EVENT_TAG_NAME)) {
                NodeList propList = node.getChildNodes();
                LogEvent event = new LogEvent();
                for (int j = 0; j < propList.getLength(); j++) {
                    Node propNode = propList.item(j);
                    NamedNodeMap attributes = propNode.getAttributes();
                    if (attributes  != null) { // tag with non emty attribute means prop tag found
                        RegExp.PropTypes propName = LogEvent.parseProp(attributes.getNamedItem(LogConfig.PROP_NAME).getNodeValue());
                        event.setProp(propName);
                        event.setVal(propName, attributes.getNamedItem(LogConfig.VAL_NAME).getNodeValue());
                        event.setComparison(propName, propNode.getNodeName());
                    }
                }
                events.add(event);
            } else if (nodeName.matches("(?i)" + LogConfig.PROPERTY_TAG_NAME)) {
                addLogProperty(node.getAttributes().getNamedItem(LogConfig.PROPERTY_PROP_NAME).getNodeValue());
            }
        }
    }

}
