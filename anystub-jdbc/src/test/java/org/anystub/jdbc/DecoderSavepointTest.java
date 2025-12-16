package org.anystub.jdbc;


import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.sql.Savepoint;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecoderSavepointTest {

    @Test
    public void testDecode() throws SQLException {
        Savepoint decode = new DecoderSavepoint().decode(asList("xx", "1", "name"));
        assertEquals(1, decode.getSavepointId());
        assertEquals("name", decode.getSavepointName());
    }
}