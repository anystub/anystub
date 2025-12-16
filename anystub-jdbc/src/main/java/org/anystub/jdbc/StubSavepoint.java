package org.anystub.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

public class StubSavepoint implements Savepoint {

    private final int id;
    private final String name;

    public StubSavepoint(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getSavepointId() throws SQLException {
        return id;
    }

    @Override
    public String getSavepointName() throws SQLException {
        return name;
    }
}
