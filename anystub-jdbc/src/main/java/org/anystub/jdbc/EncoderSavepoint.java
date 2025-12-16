package org.anystub.jdbc;

import org.anystub.Encoder;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;

public class EncoderSavepoint implements Encoder<Savepoint> {
    @Override
    public Iterable<String> encode(Savepoint savepoint) {
        try {
            return asList("Savepoint", String.valueOf(savepoint.getSavepointId()), savepoint.getSavepointName());
        } catch (SQLException e) {
            throw new NoSuchElementException("bad Savepoint: " + e.getMessage());
        }
    }
}
