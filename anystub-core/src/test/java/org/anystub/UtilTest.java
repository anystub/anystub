package org.anystub;


import org.junit.jupiter.api.Test;

import static org.anystub.StringUtil.escapeCharacterString;
import static org.anystub.StringUtil.isText;
import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void testIsTextTest() {
        assertTrue(isText("thisistextline"));
        assertTrue(isText("{\"this is\": \'text' }; line"));
        assertFalse(isText("thisistextline" + (char) 0x03));

        assertTrue(isText(""));
        assertTrue(isText("\n\n\n"));
        assertTrue(isText("123\n123"));
        assertTrue(isText("123\n\n\n\n123"));
        assertFalse(isText("123" + (char) 0x01 + "\n\n\n\n123"));
    }


    @Test
    void testIsText1() {

        assertTrue(isText("thisistextline".getBytes()));
        assertTrue(isText("{\"this is\": \'text' }; line".getBytes()));
        assertFalse(isText(("thisistextline" + (char) 0x03).getBytes()));
        assertTrue(isText("".getBytes()));
        assertTrue(isText("\n\n\n".getBytes()));
        assertTrue(isText("123\n123".getBytes()));
        assertTrue(isText("123\n\n\n\n123".getBytes()));
    }

    @Test
    void testToCharacterStringTest() {
        String s;
        s = StringUtil.toCharacterString("thisistextline".getBytes());
        assertEquals("thisistextline", s);
        s = StringUtil.toCharacterString("BASE".getBytes());
        assertEquals("TEXT BASE", s);
        s = StringUtil.toCharacterString(("thisistextline" + (char) 0x03).getBytes());
        assertTrue(s.startsWith("BASE64 "));
    }

    @Test
    void testEscapeTest() {
        assertEquals("", escapeCharacterString(""));
        assertEquals("TEXT TEXT", escapeCharacterString("TEXT"));
        assertEquals("TEXT BASE", escapeCharacterString("BASE"));
        assertEquals("BAS", escapeCharacterString("BAS"));
    }

}