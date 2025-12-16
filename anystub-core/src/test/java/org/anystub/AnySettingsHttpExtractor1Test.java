package org.anystub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnySettingsHttpExtractor1Test {

    @Test
    void testDefaultHttpSettings() {
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.httpSettings();

        assertFalse(anySettingsHttp.allHeaders());
        assertFalse(anySettingsHttp.overrideGlobal());
        assertArrayEquals(new String[]{}, anySettingsHttp.headers());
        assertArrayEquals(new String[]{}, anySettingsHttp.bodyTrigger());
        assertArrayEquals(new String[]{"POST", "PUT", "DELETE"}, anySettingsHttp.bodyMethods());
    }

    @Test
    @AnyStubId
    void testHttpSettingsMissingHttpSettings() {
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.httpSettings();

        assertFalse(anySettingsHttp.allHeaders());
        assertFalse(anySettingsHttp.overrideGlobal());
        assertArrayEquals(new String[]{}, anySettingsHttp.headers());
        assertArrayEquals(new String[]{}, anySettingsHttp.bodyTrigger());
        assertArrayEquals(new String[]{"POST", "PUT", "DELETE"}, anySettingsHttp.bodyMethods());
    }

    @Test
    @AnySettingsHttp(headers = "auth")
    void testHttpSettingsNoAnystub() {
        AnySettingsHttp anySettingsHttp = AnySettingsHttpExtractor.httpSettings();

        assertFalse(anySettingsHttp.allHeaders());
        assertFalse(anySettingsHttp.overrideGlobal());
        assertArrayEquals(new String[]{"auth"}, anySettingsHttp.headers());
        assertArrayEquals(new String[]{}, anySettingsHttp.bodyTrigger());
        assertArrayEquals(new String[]{"POST", "PUT", "DELETE"}, anySettingsHttp.bodyMethods());
    }

}