package org.anystub.jdbc;

import org.anystub.Decoder;
import org.anystub.Encoder;
import org.anystub.Supplier;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import static java.util.Arrays.asList;

public class StubConnection implements Connection {

    private static Logger log = Logger.getLogger(StubConnection.class.getName());

    private final StubDataSource stubDataSource;
    private Connection realConnection = null;
    private final LinkedList<Step> postponeTasks = new LinkedList<>();
    private final Map<String, Integer> callCounters = new HashMap<>();

    public StubConnection(StubDataSource stubDataSource) throws SQLException {

        this.stubDataSource = stubDataSource;

        add(() -> {
            realConnection = stubDataSource.getRealDataSource().getConnection();
        });
    }

    public StubConnection(String username, String password, StubDataSource stubDataSource) throws SQLException {
        this.stubDataSource = stubDataSource;
        add(() -> {
            realConnection = stubDataSource.getRealDataSource().getConnection(username, password);
        });
    }

    @Override
    public Statement createStatement() throws SQLException {
        return spy(new StubStatement(this));
    }

    @Override
    public PreparedStatement prepareStatement(String s) throws SQLException {
        return spy(new StubPreparedStatement(this, s));
    }

    @Override
    public CallableStatement prepareCall(String s) throws SQLException {
        return spy(new StubCallableStatement(this, s));
    }

    @Override
    public String nativeSQL(String s) throws SQLException {
        return getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 runSql();
                                 return getRealConnection().nativeSQL(s);
                             }
                         },
                        s);
    }

    @Override
    public void setAutoCommit(boolean b) throws SQLException {
        add(() -> {
            getRealConnection().setAutoCommit(b);
        });
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {
        add(() -> getRealConnection().commit());
    }

    @Override
    public void rollback() throws SQLException {
        add(() -> getRealConnection().rollback());
    }

    @Override
    public void close() throws SQLException {
        if (realConnection != null) {
            runSql();
            realConnection.close();
            realConnection = null;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  runSql();
                                  return getRealConnection().isClosed();
                              }
                          },
                        "isClosed");
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return spy(new StubDatabaseMetaData(this));
    }

    @Override
    public void setReadOnly(boolean b) throws SQLException {
        add(() -> {
            getRealConnection().setReadOnly(b);
        });
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        runSql();
                        return getRealConnection().isReadOnly();
                    }
                }, callKey("isReadOnly", "-"));
    }

    @Override
    public void setCatalog(String s) throws SQLException {
        add(() -> {
            getRealConnection().setCatalog(s);
        });
    }

    @Override
    public String getCatalog() throws SQLException {
        return getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                    @Override
                    public String get() throws SQLException {
                        runSql();
                        return getRealConnection().getCatalog();
                    }
                }, "getCatalog");
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException {
        add(() -> {
            getRealConnection().setTransactionIsolation(i);
        });
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        runSql();
                        return getRealConnection().getTransactionIsolation();
                    }
                }, callKey("getTransactionIsolation", "-"));
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        add(() -> {
            getRealConnection().clearWarnings();
        });
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return new StubStatement(this, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException {
        return new StubPreparedStatement(this, s, i, i1);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1) throws SQLException {
        return new StubCallableStatement(this, i, i1);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        add(() -> {
            getRealConnection().setTypeMap(map);
        });
    }

    @Override
    public void setHoldability(int i) throws SQLException {
        add(() -> {
            getRealConnection().setHoldability(i);
        });
    }

    @Override
    public int getHoldability() throws SQLException {
        return getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        runSql();
                        return getRealConnection().getHoldability();
                    }
                }, callKey("getHoldability", "-"));
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return getStubDataSource()
                .getBase()
                .request2(new Supplier<Savepoint, SQLException>() {
                              @Override
                              public Savepoint get() throws SQLException {
                                  runSql();
                                  return getRealConnection().setSavepoint();
                              }
                          },
                        new Decoder<Savepoint>() {
                            @Override
                            public Savepoint decode(Iterable<String> values) {
                                Iterator<String> iterator = values.iterator();
                                iterator.next();
                                return new StubSavepoint(Integer.parseInt(iterator.next()), iterator.next());
                            }
                        },
                        new Encoder<Savepoint>() {
                            @Override
                            public Iterable<String> encode(Savepoint savepoint) {
                                try {
                                    return asList("Savepoint", String.valueOf(savepoint.getSavepointId()), savepoint.getSavepointName());
                                } catch (SQLException e) {
                                    throw new NoSuchElementException("bad Savepoint: "+e.getMessage());
                                }
                            }
                        },
                        callKey("getHoldability", "-"));
    }

    @Override
    public Savepoint setSavepoint(String s) throws SQLException {
        return getStubDataSource()
                .getBase()
                .request2(new Supplier<Savepoint, SQLException>() {
                              @Override
                              public Savepoint get() throws SQLException {
                                  runSql();
                                  return getRealConnection().setSavepoint(s);
                              }
                          },
                        new Decoder<Savepoint>() {
                            @Override
                            public Savepoint decode(Iterable<String> values) {
                                Iterator<String> iterator = values.iterator();
                                iterator.next();
                                return new StubSavepoint(Integer.parseInt(iterator.next()), iterator.next());
                            }
                        },
                        new Encoder<Savepoint>() {
                            @Override
                            public Iterable<String> encode(Savepoint savepoint) {
                                try {
                                    return asList("Savepoint", String.valueOf(savepoint.getSavepointId()), savepoint.getSavepointName());
                                } catch (SQLException e) {
                                    throw new NoSuchElementException("bad Savepoint: "+e.getMessage());
                                }
                            }
                        },
                        callKey("getHoldability", s));
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        add(() -> {
            getRealConnection().rollback(savepoint);
        });
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        add(() -> {
            getRealConnection().releaseSavepoint(savepoint);
        });
    }

    @Override
    public Statement createStatement(int i, int i1, int i2) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int i) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String s) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String s, Object[] objects) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String s, Object[] objects) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String s) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int i) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }


    private <T extends Statement> T spy(T statement) {
        Spier spier = SpierProvider.getSpier();
        return spier == null ? statement : (T) spier.spy(statement);
    }

    private <T extends DatabaseMetaData> T spy(T metadata) {
        Spier spier = SpierProvider.getSpier();
        return spier == null ? metadata : (T) spier.spy(metadata);
    }

    public StubDataSource getStubDataSource() {
        return stubDataSource;
    }

    public Connection getRealConnection() {
        return realConnection;
    }

    public void add(Step runnable) throws SQLException {
        postponeTasks.add(runnable);
    }


    public void runSql() throws SQLException {
        while (!postponeTasks.isEmpty()) {
            try {
                Objects.requireNonNull(postponeTasks.pollFirst()).call();
            } catch (SQLException e) {
                throw e;
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

    }

    public String[] callKey(String callName, String a, String... id) {
        Integer orDefault = callCounters.getOrDefault(callName, 0);
        callCounters.put(callName, orDefault + 1);
        String[] strings = new String[id.length + 1];
        System.arraycopy(strings, 0, id, 0, id.length);
        strings[strings.length - 1] = String.format("%s%s#%d", callName, a, orDefault);
        return strings;
    }

}
