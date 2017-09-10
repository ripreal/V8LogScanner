package org.v8LogScanner.logsCfg;

import com.sun.jndi.toolkit.url.Uri;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.rgx.RegExp;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogBuilder {

    private List<LogEvent> events = new ArrayList<>();
    private List<LogLocation> locations = new ArrayList<>();
    private List<LogProperty> properties = new ArrayList<>();
    private LogDump dumps = new LogDump();
    private List<String> cfgPaths = null;

    public LogBuilder() {
        cfgPaths = LogConfig.cfgPaths();
    }

    public LogBuilder(List<String> cfgPaths) {
        this.cfgPaths = cfgPaths;
    }

    private boolean allowPLanSql = false;

    public File buildXmlFile() {
        File logFile = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            // root elements
            Document doc = docFactory.newDocumentBuilder().newDocument();

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

                    Element eventPropEl = doc.createElement(event.getComparison());
                    eventPropEl.setAttribute(LogConfig.PROP_NAME, event.getProp());
                    eventPropEl.setAttribute(LogConfig.VAL_NAME, event.getVal());

                    eventEl.appendChild(eventPropEl);
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
                removeNodeElement(logFile, ".*dummy.*");
            };
        } catch (Exception e) {
            e.printStackTrace();
            // no need to handle this
        }
        return logFile;
    }

    public File buildAllEvents() {
        return this
        .addEvent(LogEvent.LogEventComparisons.ne, RegExp.PropTypes.Event, "")
        .addLogProperty("all")
        .buildXmlFile();
    }

    public File buildSQlEvents() {
        return this
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "dbmssql")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "dbpostgrs")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "db2")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "dboracle")
        .addEvent(LogEvent.LogEventComparisons.eg, RegExp.PropTypes.Event, "excp")
        .addLogProperty("all")
        .buildXmlFile();
    }

    public final LogBuilder addLocLocation(String location, int history) {
        locations.add(new LogLocation(location, history));
        return this;
    }

    public final LogBuilder addLocLocation() {
        locations.add(new LogLocation());
        return this;
    }

    public final LogBuilder addEvent(LogEvent.LogEventComparisons comparison, RegExp.PropTypes event, String  val) {
        events.add(new LogEvent(comparison, event, val));
        return this;
    }

    public final LogBuilder addEvent() {
        events.add(new LogEvent());
        return this;
    }

    public final LogBuilder addLogProperty(String name) {
        properties.add(new LogProperty(name));
        return this;
    }

    public final LogBuilder setCreateDumps(boolean create) {
        dumps.setCreate(create);
        return this;
    }

    public final LogBuilder addLogProperty() {
        properties.add(new LogProperty());
        return this;
    }

    public final LogBuilder setPlanSql(boolean allow) {
        allowPLanSql = allow;
       return this;
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


}
