package org.anystub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigFileUtilTest {
    @Test
    void testNotExistCfg() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/test-doesnot-exist.yml");
        Assertions.assertArrayEquals(new String[0], load.requestMask.get());
    }

    @Test
    void testWrongProp() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/test.yml");
        Assertions.assertArrayEquals(new String[0], load.requestMask.get());
    }

    @Test
    void testProp1() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/test1.yml");
        Assertions.assertArrayEquals(new String[]{"headers"}, load.headers.get());
        Assertions.assertArrayEquals(new String[]{""}, load.bodyTrigger.get());
        Assertions.assertArrayEquals(new String[]{"password: .{2,10}\\,"}, load.requestMask.get());

        Assertions.assertFalse(load.testFilePrefix);
    }

    @Test
    void testEmptyAsNone() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/test2.yml");
        Assertions.assertArrayEquals(new String[0], load.headers.get());
        Assertions.assertNull(load.bodyTrigger);
        Assertions.assertArrayEquals(new String[]{"test"}, load.requestMask.get());
        Assertions.assertArrayEquals(new String[]{"test1", "test2"}, load.bodyMethods.get());

        Assertions.assertNull(load.testFilePrefix);
    }

    @Test
    void testLists() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/test3.yml");
        Assertions.assertArrayEquals(new String[]{"headers"}, load.headers.get());
        Assertions.assertArrayEquals(new String[]{"http", "http2"}, load.bodyTrigger.get());
        Assertions.assertArrayEquals(new String[]{"password: .{2,10}\\,",
                "test",
                "test2",
                "test4"
        }, load.requestMask.get());
        Assertions.assertArrayEquals(new String[]{"test1", "test2"}, load.bodyMethods.get());

        load = ConfigFileUtil.load("src/test/resources/test4.yml");
        assertNotNull(load);
        load = ConfigFileUtil.load("src/test/resources/test5.yml");
        assertNotNull(load);
        load = ConfigFileUtil.load("src/test/resources/test-badyml.yml");
        assertNotNull(load);

    }

    @Test
    void testBodyMethods() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/testBodyMethods.yml");

        Assertions.assertArrayEquals(new String[]{"CUSTOM"}, load.bodyMethods.get());
    }


    @Test
    void testFilePrefix() {
        ConfigFileUtil.AnystubCfg load = ConfigFileUtil.load("src/test/resources/testFilePrefix.yml");

        Assertions.assertTrue(load.testFilePrefix);
    }


    @Test
    void testLoadsConfig()  {
        TestSettings load = ConfigFileUtil.get("config1");
        assertNotNull(load);
        assertArrayEquals(new String[]{"TEST"},load.headers);
    }
    @Test
    void testDefaultConfig()  {
        TestSettings load = ConfigFileUtil.get(".config.yml");
        assertNotNull(load);
        assertArrayEquals(new String[]{},load.headers);
    }
    @Test
    void testMissingConfig()  {
        TestSettings load = ConfigFileUtil.get(".config-missing.yml");
        assertNotNull(load);
        assertArrayEquals(new String[]{"POST","PUT","DELETE"},load.bodyMethods);
    }

    @Test
    void testBadConfig()  {
        TestSettings load = ConfigFileUtil.get("");
        assertNotNull(load);
        assertArrayEquals(new String[]{"POST","PUT","DELETE"},load.bodyMethods);
    }

}