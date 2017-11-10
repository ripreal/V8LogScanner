package org.v8LogScanner.testV8LogScanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.v8LogScanner.commonly.fsys;
import org.v8LogScanner.logsCfg.LogBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestLogBuilder {

    private LogBuilder builder;

    @Before
    public void setup() {
        Path CFG_PATHS = Paths.get("");
        List<Path> paths = new ArrayList<>();
        paths.add(CFG_PATHS);
        builder = new LogBuilder(paths);
    }

    @After
    public void clean() {
        builder.clearAll();
    }

    @Test
    public void testReadCfgFile() {

        String init_content = builder
            .addLocLocation()
            .buildEverydayEvents()
            .getContent();

        builder.clearAll();
        String parse_content = builder
            .readCfgFile(init_content)
            .getContent();

        assertTrue(!init_content.isEmpty());
    }

    @Test
    public void testWriteToXmlFile() throws Exception {

        String testFileName = fsys.createTempFile("");
        List<Path> cfgs = new ArrayList<>(1);
        cfgs.add(new File(testFileName).toPath());
        builder = new LogBuilder(cfgs);

        File file = builder
            .addLocLocation()
            .buildAllEvents()
            .writeToXmlFile();

        assertTrue(file.exists());

        fsys.deleteFile(testFileName);
    }

    @Test
    public void testBuildAllEvents() throws Exception {

        String content = builder
            .addLocLocation()
            .buildAllEvents()
            .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildSQlEvents() throws Exception {

        String content = builder
                .addLocLocation()
                .buildSQlEvents()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildExcpEvents() throws Exception {

        String content =  builder
                .addLocLocation()
                .buildExcpEvents()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildEverydayEvents() throws Exception {

        String content =  builder
                .addLocLocation()
                .buildEverydayEvents()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildLongEvents() throws Exception {
        String content =  builder
                .addLocLocation()
                .buildLongEvents()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testInvestigateNonEffectiveQueries() throws Exception {
        String content =  builder
                .addLocLocation()
                .buildInvestigateSQlQueries("DefUser", "test")
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuild1cLocksEvents() throws Exception {
        String content = builder
                .addLocLocation()
                .build1cLocksEvents()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildSqlLocksEvents() throws Exception {
        String content = builder
                .addLocLocation()
                .buildSqlDeadLocksEvents()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildFindTableByObjectID() throws Exception {
        String content = builder
                .addLocLocation()
                .buildFindTableByObjectID("test")
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testBuildCircularRefsAndEXCP() throws Exception {
        String content = builder
                .addLocLocation()
                .buildCircularRefsAndEXCP()
                .getContent();

        assertTrue(!content.isEmpty());
    }

    @Test
    public void testSetHistory() throws IOException {

        int LOG_HISTORY = 22;
        Path CFG_PATHS = Paths.get("");

        builder.setCfgPaths(CFG_PATHS);

        String content = builder
            .addLocLocation()
            .setHistory(LOG_HISTORY)
            .getContent();

        builder.clearAll();
        builder.readCfgFile(content);

        assertTrue(builder.getHistory().compareTo(String.valueOf(LOG_HISTORY)) == 0);

    }

}
