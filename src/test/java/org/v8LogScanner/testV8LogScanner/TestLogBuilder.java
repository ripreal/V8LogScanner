package org.v8LogScanner.testV8LogScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.logsCfg.LogBuilder;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TestLogBuilder {

    private LogBuilder builder;
    private String testFileName = "";

    @Before
    public void setup() {
        testFileName = fsys.createTempFile("");

        List<Path> cfgs = new ArrayList<>(1);
        cfgs.add(new File(testFileName).toPath());
        builder = new LogBuilder(cfgs);
    }

    @After
    public void clean() {
        builder.clearAll();
        fsys.deleteFile(testFileName);
    }

    @Test
    public void testReadCfgFile() {
        /* TEST ONLY WORKS LOCALLY, NOT IN TRAVIS
        testFileName = fsys.createTempFile("");

        File file = builder
            .addLocLocation()
            .buildEverydayEvents()
            .writeToXmlFile();
        String source_res = fsys.readAllFromFile(file.toPath());

        file = builder
            .setCfgPaths(file.toPath())
            .readCfgFile()
            .writeToXmlFile();
        String parse_res = fsys.readAllFromFile(file.toPath());

        assertTrue(source_res.compareTo(parse_res) == 0);
        */
    }

    @Test
    public void testBuildAllEvents() throws Exception {

        File file = builder
                .addLocLocation()
                .buildAllEvents()
                .writeToXmlFile();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildSQlEvents() throws Exception {

        File file = builder
                .addLocLocation(testFileName, "1")
                .buildSQlEvents()
                .writeToXmlFile();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildExcpEvents() throws Exception {

        File file = builder
                .addLocLocation(testFileName, "1")
                .buildExcpEvents()
                .writeToXmlFile();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildEverydayEvents() throws Exception {

        File file = builder
                .addLocLocation(testFileName, "1")
                .buildEverydayEvents()
                .writeToXmlFile();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildLongEvents() throws Exception {
        File file = builder
                .addLocLocation(testFileName, "1")
                .buildLongEvents()
                .writeToXmlFile();
        assertTrue(file.exists());
    }

    @Test
    public void testInvestigateNonEffectiveQueries() throws Exception {
        File file = builder
                .addLocLocation(testFileName, "1")
                .buildInvestigateSQlQueries("DefUser", "test")
                .writeToXmlFile();
        assertTrue(file.exists());
    }

    @Test
    public void testBuild1cLocksEvents() throws Exception {
        File file = builder
                .addLocLocation(testFileName, "1")
                .build1cLocksEvents()
                .writeToXmlFile();
        assertTrue(file.exists());
    }

    @Test
    public void testBuildSqlLocksEvents() throws Exception {
        File file = builder
                .addLocLocation(testFileName, "1")
                .buildSqlDeadLocksEvents()
                .writeToXmlFile();
        assertTrue(file.exists());
    }

    @Test
    public void testBuildFindTableByObjectID() throws Exception {
        File file = builder
                .addLocLocation(testFileName, "1")
                .buildFindTableByObjectID("test")
                .writeToXmlFile();
        assertTrue(file.exists());
    }

    @Test
    public void testBuildCircularRefsAndEXCP() throws Exception {
        File file = builder
                .addLocLocation(testFileName, "1")
                .buildCircularRefsAndEXCP()
                .writeToXmlFile();
        assertTrue(file.exists());
    }
}
