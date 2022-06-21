package org.anystub.http;


import org.anystub.AnySettingsHttp;
import org.anystub.AnySettingsHttpExtractor;
import org.anystub.AnyStubFileLocator;
import org.anystub.AnyStubId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AnySettingsHttp(headers = "classHeader")
public class AnySettingsHttpExtractorTest {

    @Test
    public void testdiscoverSettingsClass() {
        AnyStubId s = AnyStubFileLocator.discoverFile();
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.discoverSettings();
        assertNotNull(anySettingsHttp);
        assertFalse(anySettingsHttp.allHeaders());
        assertArrayEquals(new String[]{"classHeader"}, anySettingsHttp.headers());
    }
    @Test
    @AnySettingsHttp(headers = {"test1", "test2"}, allHeaders = false, bodyTrigger = {"http://", "234"})
    public void testdiscoverSettings1() {
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.discoverSettings();
        assertNotNull(anySettingsHttp);
        Object headers = new String[]{"test1", "test2"};
        assertFalse(anySettingsHttp.allHeaders());
        assertArrayEquals(new String[]{"test1", "test2"}, anySettingsHttp.headers());
        assertArrayEquals(new String[]{"http://", "234"}, anySettingsHttp.bodyTrigger());
    }

    @Test
    @AnySettingsHttp(headers = {})
    public void testdiscoverSettings2() {
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.discoverSettings();
        assertNotNull(anySettingsHttp);
        Object headers = new String[]{"test1", "test2"};
        assertFalse(anySettingsHttp.allHeaders());
        assertArrayEquals(new String[]{}, anySettingsHttp.headers());
    }

    @Test
    @AnySettingsHttp(allHeaders = true)
     void testSettingsInTestLambda() {
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.discoverSettings();
        assertNotNull(anySettingsHttp);
        assertTrue(anySettingsHttp.allHeaders());

        Runnable r = () -> {
            AnySettingsHttp anySettingsHttp1 = AnySettingsHttpExtractor.discoverSettings();
            assertNotNull(anySettingsHttp1);
            assertTrue(anySettingsHttp1.allHeaders());
        };

        r.run();
    }

}