package org.anystub.jdbc;

import org.anystub.Decoder;
import org.anystub.Encoder;
import org.anystub.Supplier;
import org.anystub.StringUtil;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingFormatArgumentException;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

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
        return getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        runSql();
                        return getRealConnection().getAutoCommit();
                    }
                }, callKey("getAutoCommit", "-"));
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
        return new StubCallableStatement(this, s, i, i1);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return getStubDataSource()
                .getBase()
                .request2(new Supplier<Map<String, Class<?>>, SQLException>() {
                              @Override
                              public Map<String, Class<?>> get() throws SQLException {
                                  runSql();
                                  return getRealConnection().getTypeMap();
                              }
                          },
                        new Decoder<Map<String, Class<?>>>() {
                            @Override
                            public Map<String, Class<?>> decode(Iterable<String> values) {
                                Map<String, Class<?>> res = new HashMap<>();
                                Iterator<String> it = values.iterator();
                                while (it.hasNext()) {
                                    String key = it.next();
                                    Class<?> value = StringUtil.decode(it.next());
                                    res.put(key, value);
                                }
                                return res;
                            }
                        },
                        new Encoder<Map<String, Class<?>>>() {
                            @Override
                            public Iterable<String> encode(Map<String, Class<?>> stringClassMap) {
                                List<String> res = new LinkedList<>();
                                for (Map.Entry<String, Class<?>> o : stringClassMap.entrySet()) {
                                    res.add(o.getKey());
                                    res.add(StringUtil.encode(o.getValue()));
                                }
                                return res;
                            }
                        },
                        callKey("getSchema"));
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
                }, callKey("getHoldability"));
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
                        new DecoderSavepoint(),
                        new EncoderSavepoint(),
                        callKey("getHoldability"));
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
                        new DecoderSavepoint(),
                        new EncoderSavepoint(),
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
        return spy(new StubStatement(this, i, i1, i2));
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException {
        return spy(new StubPreparedStatement(this, s, i, i1, i2));
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException {
        return spy(new StubCallableStatement(this, s, i, i1, i2));
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i) throws SQLException {
        return spy(new StubPreparedStatement(this, s, i));
    }

    @Override
    public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException {
        return spy(new StubPreparedStatement(this, s, ints));
    }

    @Override
    public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException {
        return spy(new StubPreparedStatement(this, s, strings));
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
        return getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        runSql();
                        return getRealConnection().isValid(i);
                    }
                }, callKey("isValid", String.valueOf(i)));
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        try {
            add(() -> {
                getRealConnection().setClientInfo(s, s1);
            });
        } catch (SQLException e) {
            throw new SQLClientInfoException(new TreeMap<>(), e);
        }
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        try {
            add(() -> {
                getRealConnection().setClientInfo(properties);
            });
        } catch (SQLException e) {
            throw new SQLClientInfoException(new TreeMap<>(), e);
        }
    }

    @Override
    public String getClientInfo(String s) throws SQLException {
        return getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 runSql();
                                 return getRealConnection().getClientInfo(s);
                             }
                         },
                        callKey("getClientInfo", s));
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

        add(() -> {
            getRealConnection().setSchema(s);
        });
    }

    @Override
    public String getSchema() throws SQLException {
        return getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 runSql();
                                 return getRealConnection().getSchema();
                             }
                         },
                        callKey("getSchema"));
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        add(() -> {
            getRealConnection().abort(executor);
        });
    }

    @Override
    public void setNetworkTimeout(Executor executor, int i) throws SQLException {
        add(() -> {
            getRealConnection().setNetworkTimeout(executor, i);
        });
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  runSql();
                                  return getRealConnection().getNetworkTimeout();
                              }
                          },
                        callKey("getNetworkTimeout"));
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        runSql();
                        return getRealConnection().isWrapperFor(aClass);
                    }
                }, callKey("isWrapperFor", aClass.getCanonicalName()));
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

    public String[] callKey(String... details) {
        if (details.length < 1) {
            throw new MissingFormatArgumentException("details must include at least one string");
        }
        String key = Arrays.toString(details);
        Integer orDefault = callCounters.getOrDefault(key, 0);
        callCounters.put(key, orDefault + 1);

        if (orDefault == 0) {
            return details;
        }
        String[] strings;
        strings = new String[details.length + 1];
        System.arraycopy(details, 0, strings, 0, details.length);
        strings[strings.length - 1] = String.format("#%d", orDefault);
        return strings;

    }

}
