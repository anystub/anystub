package org.anystub.jdbc;

import org.anystub.Base;
import org.anystub.Decoder;
import org.anystub.Encoder;
import org.anystub.Supplier;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StubStatement implements Statement {
    protected StubConnection stubConnection;
    private Statement realStatement = null;
    private List<String> keys = new LinkedList<>();


    public StubStatement(StubConnection stubConnection) throws SQLException {

        this.stubConnection = stubConnection;
        stubConnection.add(() -> {
            realStatement = this.stubConnection.getRealConnection().createStatement();
        });
    }

    protected StubStatement(boolean noRealRequred, StubConnection stubConnection) {

        this.stubConnection = stubConnection;
    }

    @Override
    public ResultSet executeQuery(String s) throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String s) throws SQLException {
        return 0;
    }

    @Override
    public void close() throws SQLException {
        // if there is a real connection - need to call it
        if (getRealStatement() != null) {
            stubConnection.runSql();
            getRealStatement().close();
//            realStatement = null;
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int i) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int i) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int i) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String s) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setCursorName(s);
        });

    }

    @Override
    public boolean execute(String s) throws SQLException {
        Base base = stubConnection.getStubDataSource().getBase();

        return base.requestB(new Supplier<Boolean, SQLException>() {
            @Override
            public Boolean get() throws SQLException {
                stubConnection.runSql();
                return getRealStatement().execute(s);
            }
        }, s);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int i) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String s) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        Base base = stubConnection.getStubDataSource().getBase();

        return base.request2(new Supplier<int[], SQLException>() {
                                 @Override
                                 public int[] get() throws SQLException {
                                     stubConnection.runSql();
                                     return getRealStatement().executeBatch();
                                 }
                             },
                new Decoder<int[]>() {
                    @Override
                    public int[] decode(Iterable<String> values) {
                        List<Integer> collect = StreamSupport.stream(values.spliterator(), false)
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
                        int[] r = new int[collect.size()];

                        for (int i = 0; i < r.length; i++) {
                            r[i] = collect.get(i);
                        }
                        return r;
                    }
                }, new Encoder<int[]>() {
                    @Override
                    public Iterable<String> encode(int[] values) {
                        String[] s = new String[values.length];
                        for (int i = 0; i < values.length; i++) {
                            s[i] = String.valueOf(values[i]);
                        }
                        return Arrays.asList(s);
                    }
                },

                useKeys());


    }

    @Override
    public Connection getConnection() throws SQLException {
        return stubConnection;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean b) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }


    protected void addKeys(String... keys) {
        for (int i = 0; i < keys.length; i++) {
            this.keys.add(keys[i]);
        }
    }

    protected String[] useKeys() {
        String[] requestsKeys = keys.toArray(new String[0]);
        keys.clear();
        return requestsKeys;
    }

    protected Statement getRealStatement() {
        return realStatement;
    }

}
