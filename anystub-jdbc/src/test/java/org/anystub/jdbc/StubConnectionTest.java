package org.anystub.jdbc;


import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


public class StubConnectionTest {

    @Test
    public void testCallKey() throws SQLException {
        StubConnection instance = new StubConnection(null);

        String[] strings;
        strings = instance.callKey("test", "next");

        assertArrayEquals(new String[]{"test", "next"}, strings);

        strings = instance.callKey("test", "next");
        assertArrayEquals(new String[]{"test", "next", "#1"}, strings);

        strings = instance.callKey("test", "next");
        assertArrayEquals(new String[]{"test", "next", "#2"}, strings);

    }
}