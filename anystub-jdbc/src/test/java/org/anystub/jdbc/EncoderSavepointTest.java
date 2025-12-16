package org.anystub.jdbc;


import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EncoderSavepointTest {
    @Test
    public void testEncodeTest() {
        StubSavepoint stubSavepoint = new StubSavepoint(1, "2");
        Iterator<String> encode = new EncoderSavepoint().encode(stubSavepoint).iterator();
        assertEquals("Savepoint",encode.next());
        assertEquals("1",encode.next());
        assertEquals("2",encode.next());
    }

}