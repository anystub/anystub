package org.anystub;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AnyStubId
class AnyStubFileLocatorTest {

    @Test
    @AnyStubId
    void testcase() {
        assertEquals("AnyStubFileLocatorTest-testcase.yml", AnyStubFileLocator.discoverFile().filename());
    }

    @Test
    @AnyStubId(filename = "special")
    void testcase1() {
        assertEquals("special.yml", AnyStubFileLocator.discoverFile().filename());
    }

    @Test
    void testname() {
        assertEquals("AnyStubFileLocatorTest-testname.yml", AnyStubFileLocator.discoverFile().filename());

    }

    @Test
    @AnyStubId(filename = "test.yml")
    void testExtname() {
        assertEquals("test.yml", AnyStubFileLocator.discoverFile().filename());

    }

    @Test
    void testCombineArrays() {
        String[] strings = AnyStubFileLocator.combineArrays(new String[]{"one"}, new String[]{"one", "two"});

        assertArrayEquals(new String[]{"one", "two"}, strings);
    }

    
}