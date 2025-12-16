package org.anystub;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AnyStubFileLocator1Test {
    @Test
    void testname() {
        assertNull(AnyStubFileLocator.discoverFile());

    }

    @Test
    @AnyStubId
    void testName1() {
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        assertNotNull(anyStubId);
        assertEquals("AnyStubFileLocator1Test-testName1.yml", anyStubId.filename());
    }
    @Test
    @AnyStubId(filename = "filename2")
    void testName2() {
        AnyStubId anyStubId = AnyStubFileLocator.discoverFile();
        assertNotNull(anyStubId);
        assertEquals("filename2.yml", anyStubId.filename());
    }

}