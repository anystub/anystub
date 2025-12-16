package org.anystub;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SettingsUtilTest {

    @Test
    @AnyStubId
    @AnySettingsHttp(bodyTrigger = "local")
    void methodBodyRuleTest() {
        boolean local_glo = SettingsUtil.matchBodyRule("POST", "local glo");
        assertTrue(local_glo);
    }

    @Test
    @AnyStubId
    @AnySettingsHttp
    void testNotBodyMethod() {
        boolean local_glo;
        local_glo = SettingsUtil.matchBodyRule("GET", "local glo");
        assertFalse(local_glo);
        local_glo = SettingsUtil.matchBodyRule("CUSTOM", "local glo");
        assertFalse(local_glo);
    }

    @Test
    @AnyStubId
    @AnySettingsHttp(bodyTrigger = "-")
    void testExcludePostBodyMethod() {
        boolean local_glo;
        local_glo = SettingsUtil.matchBodyRule("POST", "local glo");
        assertFalse(local_glo);
    }

    @Test
    @AnyStubId
    @AnySettingsHttp(bodyTrigger = "-auth")
    void testExcludeSpecificPostBodyMethod() {
        boolean local_glo;
        local_glo = SettingsUtil.matchBodyRule("POST", "local glo");
        assertTrue(local_glo);

        local_glo = SettingsUtil.matchBodyRule("POST", "auth");
        assertFalse(local_glo);
    }

    @Test
    @AnyStubId
    @AnySettingsHttp(bodyTrigger = {"local", "-auth"})
    void methodSelectiveBodyTest() {
        boolean local_glo = SettingsUtil.matchBodyRule("POST", "local auth");
        assertFalse(local_glo);
    }

    @Test
    @AnyStubId
    void testDefaultPostBodyInclude() {
        boolean local_glo;
        local_glo = SettingsUtil.matchBodyRule("POST", "local glo");
        assertTrue(local_glo);
    }
    @Test
    @AnyStubId
    @AnySettingsHttp(bodyTrigger = "api")
    void testSelectPostBodyInclude() {
        boolean local_glo;
        local_glo = SettingsUtil.matchBodyRule("POST", "api/test");
        assertTrue(local_glo);

        local_glo = SettingsUtil.matchBodyRule("POST", "auth");
        assertFalse(local_glo);
    }

    @Test
    @AnyStubId(requestMasks = "xxx")
    @AnySettingsHttp(bodyTrigger = "local")
    void maskBodyTest() {
        String masked = SettingsUtil.maskBody("lakjsd,zmncxxx qweq");
        assertEquals("lakjsd,zmnc... qweq", masked);
    }

    @Test
    @AnyStubId(requestMasks = {"secret", "password", "....-.*\\.\\d{2,10}", "\\d{4}-\\d{1,2}-\\d{1,2}"})
    void multiMaskBodyTest() {
        String msg = String.format("hypothetical request containing a secret data like a password, "+
                        "or a variable timestamp: %s in the middle of request. date\":[%s]",
                LocalDateTime.now().toString(), LocalDate.now().toString());
        String masked = SettingsUtil.maskBody(msg);
        assertEquals("hypothetical request containing a ... data like a ..., "+
                "or a variable timestamp: ... in the middle of request. date\":[...]", masked, msg);
    }

    @Test
    void noMaskBodyTest() {

        String msg = String.format("hypothetical request containing a secret data like a password, "+
                        "or a variable timestamp: %s in the middle of request. date\":[%s]",
                LocalDateTime.now().toString(), LocalDate.now().toString());
        String masked = SettingsUtil.maskBody(msg);
        Assertions.assertFalse(masked.contains("..."));
    }
}