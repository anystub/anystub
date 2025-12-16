package org.anystub.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;

/**
 * to wrap objects with mokito.spy()
 */
public interface Spier {

    Connection spy(Connection connection);
    Statement spy(Statement statement);
    DatabaseMetaData spy(DatabaseMetaData databaseMetaData);
}
