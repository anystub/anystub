package org.anystub.jdbc;

import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class StubConnectionTest {

    @Test
    public void callKey() throws SQLException {
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