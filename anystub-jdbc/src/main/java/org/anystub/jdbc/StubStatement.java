package org.anystub.jdbc;

import org.anystub.Decoder;
import org.anystub.Encoder;
import org.anystub.KeysSupplier;
import org.anystub.Supplier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class StubStatement implements Statement {
    protected StubConnection stubConnection;
    private Statement realStatement = null;
    private final List<String> batch = new LinkedList<>();
    private String executedCommand = null;


    public StubStatement(StubConnection stubConnection) throws SQLException {

        this.stubConnection = stubConnection;
        stubConnection.add(() -> {
            realStatement = this.stubConnection.getRealConnection().createStatement();
        });
    }

    protected StubStatement(boolean noRealRequired, StubConnection stubConnection) {

        if (!noRealRequired) {
            throw new UnsupportedOperationException();
        }
        this.stubConnection = stubConnection;
    }

    public StubStatement(StubConnection stubConnection, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.stubConnection = stubConnection;
        stubConnection.add(() -> {
            realStatement = this
                    .stubConnection
                    .getRealConnection()
                    .createStatement(resultSetType, resultSetConcurrency);
        });
    }

    public StubStatement(StubConnection stubConnection, int i, int i1, int i2) throws SQLException {
        this.stubConnection = stubConnection;
        stubConnection.add(() -> {
            realStatement = this
                    .stubConnection
                    .getRealConnection()
                    .createStatement(i, i1, i2);
        });
    }

    @Override
    public ResultSet executeQuery(String s) throws SQLException {
        return produceResultSet(() -> getRealStatement().executeQuery(s), () -> new String[]{s});
//        !
//        executedCommand = s;
//        return stubConnection
//                .getStubDataSource()
//                .getBase()
//                .request2(new Supplier<ResultSet, SQLException>() {
//                              @Override
//                              public ResultSet get() throws SQLException {
//                                  stubConnection.runSql();
//                                  return getRealStatement().executeQuery(s);
//                              }
//                          },
//                        new DecoderResultSet(),
//                        new EncoderResultSet(),
//                        s);
    }

    @Override
    public int executeUpdate(String s) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().executeUpdate(s);
                    }
                }, s);
    }

    @Override
    public void close() throws SQLException {
        // if a real connection exists we need to close it
        if (getRealStatement() != null) {
            stubConnection.runSql();
            getRealStatement().close();
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getMaxFieldSize();
                    }
                }, callKey("getMaxFieldsSize"));
    }

    @Override
    public void setMaxFieldSize(int i) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setMaxFieldSize(i);
        });
    }

    @Override
    public int getMaxRows() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getMaxRows();
                    }
                }, callKey("getMaxRows"));
    }

    @Override
    public void setMaxRows(int i) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setMaxRows(i);
        });
    }

    @Override
    public void setEscapeProcessing(boolean b) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setEscapeProcessing(b);
        });
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getQueryTimeout();
                    }
                }, callKey("getQueryTimeout"));
    }

    @Override
    public void setQueryTimeout(int i) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setQueryTimeout(i);
        });
    }

    @Override
    public void cancel() throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().cancel();
        });
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().clearWarnings();
        });
    }

    @Override
    public void setCursorName(String s) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setCursorName(s);
        });

    }

    @Override
    public boolean execute(String s) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().execute(s);
                    }
                }, s);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return produceResultSet(() -> getRealStatement().getResultSet(), () -> callKey("getResultSet"));
    }


    @Override
    public int getUpdateCount() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getUpdateCount();
                    }
                }, callKey("getUpdateCount"));
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getMoreResults();
                    }
                }, callKey("getMoreResults"));
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setFetchDirection(i);
        });
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getFetchDirection();
                    }
                }, callKey("getFetchDirection"));
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setFetchDirection(i);
        });
    }

    @Override
    public int getFetchSize() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getFetchSize();
                    }
                }, callKey("getFetchSize"));
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getResultSetConcurrency();
                    }
                }, callKey("getResultSetConcurrency"));
    }

    @Override
    public int getResultSetType() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getResultSetType();
                    }
                }, callKey("getResultSetType"));
    }

    @Override
    public void addBatch(String s) throws SQLException {
        addCallBatch(s);
        stubConnection.add(() -> {
            getRealStatement().addBatch(s);
        });
    }

    @Override
    public void clearBatch() throws SQLException {
        batch.clear();
        stubConnection.add(() -> {
            getRealStatement().clearBatch();
        });
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<int[], SQLException>() {
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
                                return stream(values)
                                        .mapToObj(String::valueOf)
                                        .collect(Collectors.toList());
                            }
                        }, useCallKeys());


    }

    @Override
    public Connection getConnection() throws SQLException {
        return stubConnection;
    }

    @Override
    public boolean getMoreResults(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getMoreResults();
                    }
                }, callKey("getMoreResults", i));
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {

        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getGeneratedKeys();
                              }
                          },
                        new DecoderResultSet(),
                        new EncoderResultSet(),
                        callKey("getGeneratedKeys"));
    }

    @Override
    public int executeUpdate(String s, int i) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().executeUpdate(s, i);
                    }
                }, s, String.valueOf(i));
    }

    @Override
    public int executeUpdate(String s, int[] ints) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().executeUpdate(s, ints);
                    }
                }, s, Arrays.toString(ints));
    }

    @Override
    public int executeUpdate(String s, String[] strings) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().executeUpdate(s, strings);
                    }
                }, s, Arrays.toString(strings));
    }

    @Override
    public boolean execute(String s, int i) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().execute(s, i);
                    }
                }, s, String.valueOf(i));
    }

    @Override
    public boolean execute(String s, int[] ints) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().execute(s, ints);
                    }
                }, s, Arrays.toString(ints));
    }

    @Override
    public boolean execute(String s, String[] strings) throws SQLException {
        executedCommand = s;
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().execute(s, strings);
                    }
                }, s, Arrays.toString(strings));

    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getResultSetHoldability();
                    }
                }, callKey("getResultSetHoldability"));
    }

    @Override
    public boolean isClosed() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().isClosed();
                    }
                }, callKey("isClosed"));
    }

    @Override
    public void setPoolable(boolean b) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().setPoolable(b);
        });

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().isPoolable();
                    }
                }, callKey("isPoolable"));
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().closeOnCompletion();
        });
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().isCloseOnCompletion();
                    }
                }, callKey("isCloseOnCompletion"));
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().isWrapperFor(aClass);
                    }
                }, callKey("isCloseOnCompletion"));
    }


    protected String[] statementId() {
        if (executedCommand == null) {
            return new String[0];
        }
        return new String[]{executedCommand};
    }

    protected String[] callKey(String callName, Integer... a) {
        List<String> callKey;
        callKey = new ArrayList<>(asList(statementId()));
        callKey.add(callName);
        callKey.addAll(Arrays.stream(a)
                .map(String::valueOf)
                .collect(Collectors.toList()));

        return stubConnection.callKey(callKey.toArray(new String[0]));
    }

    protected String[] callKey(String callName, String a) {
        List<String> callKey = new ArrayList<>(asList(statementId()));
        callKey.add(callName);
        callKey.add(a);

        return stubConnection.callKey(callKey.toArray(new String[0]));
    }

    protected Statement getRealStatement() {
        return realStatement;
    }

    private void addCallBatch(String s) {
        batch.add(s);
    }


    protected String[] useCallKeys() {
        List<String> callKey = new ArrayList<>();
        callKey.add("executeBatch");
        callKey.addAll(batch);
        batch.clear();
        return callKey.toArray(new String[0]);

    }

    protected StubResultSet decodeStubResultSet(Supplier<ResultSet, SQLException> rsSupplier) {
        try {
            return new StubResultSet(stubConnection, statementId(), rsSupplier);
        } catch (SQLException e) {
            throw new UnsupportedOperationException("failed to create StubResultSet from recorded data", e);
        }
    }

    protected Iterable<String> encodeResultSetHeader(ResultSet resultSet) {
        try {
            return ResultSetUtil.encodeHeader(resultSet.getMetaData());
        } catch (SQLException e) {
            return Collections.singletonList("0");
        }
    }

    protected ResultSet produceResultSet(Supplier<ResultSet, SQLException> rsSupplier, KeysSupplier keysSupplier) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .isStubResultSetMode() ?
                produceStubResultSet(rsSupplier, keysSupplier) :
                produceSimpleResultSet(rsSupplier, keysSupplier);
    }

    /**
     * returns stubbed resultSet
     * keeps only header in stub
     *
     * @param rsSupplier
     * @param keysSupplier
     * @return
     * @throws SQLException
     */
    private ResultSet produceStubResultSet(Supplier<ResultSet, SQLException> rsSupplier, KeysSupplier keysSupplier) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return rsSupplier.get();
                              }
                          },
                        new Decoder<ResultSet>() {
                            @Override
                            public ResultSet decode(Iterable<String> values) {
                                return decodeStubResultSet(rsSupplier);
                            }
                        },
                        new Encoder<ResultSet>() {
                            @Override
                            public Iterable<String> encode(ResultSet resultSet) {
                                return encodeResultSetHeader(resultSet);
                            }
                        },

                        keysSupplier);
    }

    /**
     * returns SimpleResultSet
     * saves full result set in stub
     *
     * @param rsSupplier
     * @param keysSupplier
     * @return
     * @throws SQLException
     */
    private ResultSet produceSimpleResultSet(Supplier<ResultSet, SQLException> rsSupplier, KeysSupplier keysSupplier) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<ResultSet, SQLException>() {
                              @Override
                              public ResultSet get() throws SQLException {
                                  stubConnection.runSql();
                                  return rsSupplier.get();
                              }
                          },
                        new Decoder<ResultSet>() {
                            @Override
                            public ResultSet decode(Iterable<String> values) {
                                return ResultSetUtil.decode(values);
                            }
                        },
                        new Encoder<ResultSet>() {
                            @Override
                            public Iterable<String> encode(ResultSet resultSet) {
                                return ResultSetUtil.encode(resultSet);
                            }
                        },

                        keysSupplier);
    }

}
