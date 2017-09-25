package org.v8LogScanner.testV8LogScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.logsCfg.LogBuilder;
import org.v8LogScanner.logsCfg.LogEvent;
import org.v8LogScanner.rgx.RegExp;
import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestLogBuilder {

    private LogBuilder builder;
    private String testFileName = "";
    @Before
    public void setup() {
        testFileName = fsys.createTempFile("");

        List<String> cfgs = new ArrayList<>(1);
        cfgs.add(testFileName);
        builder = new LogBuilder(cfgs);
    }

    @After
    public void clean() {
        fsys.deleteFile(testFileName);
    }

    @Test
    public void testReadCfgFile() {

        testFileName = fsys.createTempFile("");

        File file = builder
            .addLocLocation()
            .buildAllEvents();
        String source_res = fsys.readAllFromFile(file.toPath());

        file = builder
            .readCfgFile(file.getPath())
            .writeToXmlFile();
        String parse_res = fsys.readAllFromFile(file.toPath());

        assertTrue(source_res.compareTo(parse_res) == 0);
    }

    @Test
    public void testBuildAllEvents() throws Exception {

        File file = builder
        .addLocLocation()
        .buildAllEvents();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildSQlEvents() throws Exception {

        File file = builder
        .addLocLocation(testFileName, "1")
        .buildSQlEvents();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildExcpEvents() throws Exception {

        File file = builder
        .addLocLocation(testFileName, "1")
        .buildExcpEvents();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildEverydayEvents() throws Exception {

        File file = builder
            .addLocLocation(testFileName, "1")
            .buildEverydayEvents();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildLongEvents() throws Exception {
        File file = builder
            .addLocLocation(testFileName, "1")
            .buildLongEvents();
        assertTrue(file.exists());
    }

    @Test
    public void testInvestigateNonEffectiveQueries() throws Exception {
        File file = builder
            .addLocLocation(testFileName, "1")
            .buildInvestigateNonEffectiveQueries("DefUser","test");
        assertTrue(file.exists());
    }

    @Test
    public void testBuild1cLocksEvents() throws Exception {
        File file = builder
            .addLocLocation(testFileName, "1")
            .build1cLocksEvents();
        assertTrue(file.exists());
    }
}
