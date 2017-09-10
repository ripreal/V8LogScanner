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
    public void testBuildAllEvents() throws Exception {

        File file = builder
        .addLocLocation()
        .buildAllEvents();

        assertTrue(file.exists());
    }

    @Test
    public void testBuildSQlEvents() throws Exception {

        File file = builder
        .addLocLocation(testFileName, 1)
        .buildSQlEvents();

        assertTrue(file.exists());
    }

}
