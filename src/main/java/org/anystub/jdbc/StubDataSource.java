package org.anystub.jdbc;

import org.anystub.Base;
import org.anystub.mgmt.BaseManagerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class StubDataSource implements DataSource {

    private final Logger log = Logger.getLogger("StubDataSource");
    private final DataSource realDataSource;
    private Base base = null;
    private boolean isStubResultSetMode = false;

    public StubDataSource(DataSource realDataSource) {
        this.realDataSource = realDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        StubConnection stubConnection = new StubConnection(this);
        return spy(stubConnection);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        StubConnection stubConnection = new StubConnection(username, password, this);
        return spy(stubConnection);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return getRealDataSource().getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        getRealDataSource().setLogWriter(printWriter);
    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {
        getRealDataSource().setLoginTimeout(i);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return getRealDataSource().getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return getRealDataSource().getParentLogger();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return getRealDataSource().unwrap(aClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return getRealDataSource().isWrapperFor(aClass);
    }


    private Connection spy(Connection connection) {
        return SpierProvider.getSpier() == null ? connection : SpierProvider.getSpier().spy(connection);
    }


    /**
     * returns the most appropriate stub file correspondent to current position related to the datasource
     *
     * @return stub file
     */
    public Base getBase() {
        return BaseManagerFactory.locate();
    }

    public DataSource getRealDataSource() {
        return realDataSource;
    }

    public StubDataSource setFallbackBase(Base base) {
        this.base = base;
        return this;
    }

    @Deprecated
    public StubDataSource setStubSuffix(String s) {
        return this;
    }

    /**
     * wraps datasource with a stub
     *
     * @param ds real DataSource
     * @return stubbed DataSource
     */
    public static DataSource stub(DataSource ds) {
        return new StubDataSource(ds);
    }

    public StubDataSource setStubResultSetMode(boolean stubResultSetMode) {
        isStubResultSetMode = stubResultSetMode;
        return this;
    }

    public boolean isStubResultSetMode() {
        return isStubResultSetMode;
    }
}
