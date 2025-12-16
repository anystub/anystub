package org.anystub.jdbc;

import org.anystub.Supplier;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StubResultSetMetaData implements ResultSetMetaData {

    private final StubConnection stubConnection;
    //    private final StubPreparedStatement stubPreparedStatement;
    private final String id;
    private ResultSetMetaData realResultSetMetaData = null;

    public StubResultSetMetaData(StubConnection stubConnection, String id, Supplier<ResultSetMetaData, SQLException> supplier) throws SQLException {
        this.stubConnection = stubConnection;
        this.id = id;

        stubConnection.add(() -> {
            realResultSetMetaData = supplier.get();
        });
    }

    @Override
    public int getColumnCount() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? 0 :
                                          realResultSetMetaData.getColumnCount();
                              }
                          },
                        id, "getColumnCount");
    }

    @Override
    public boolean isAutoIncrement(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isAutoIncrement(i);
                              }
                          },
                        useKeys("isAutoIncrement", i));
    }

    @Override
    public boolean isCaseSensitive(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isCaseSensitive(i);
                              }
                          },
                        useKeys("isCaseSensitive", i));
    }

    @Override
    public boolean isSearchable(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isSearchable(i);
                              }
                          },
                        useKeys("isSearchable", i));
    }

    @Override
    public boolean isCurrency(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isCurrency(i);
                              }
                          },
                        useKeys("isCurrency", i));
    }

    @Override
    public int isNullable(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? 0 :
                                          realResultSetMetaData.isNullable(i);
                              }
                          },
                        useKeys("isNullable", i));
    }

    @Override
    public boolean isSigned(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isSigned(i);
                              }
                          },
                        useKeys("isSigned", i));
    }

    @Override
    public int getColumnDisplaySize(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? 0 :
                                          realResultSetMetaData.getColumnDisplaySize(i);
                              }
                          },
                        useKeys("getColumnCount", i));
    }

    @Override
    public String getColumnLabel(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getColumnLabel(i);
                             }
                         },
                        useKeys("getColumnLabel", i));
    }

    @Override
    public String getColumnName(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getColumnName(i);
                             }
                         },
                        useKeys("getColumnName", i));
    }

    @Override
    public String getSchemaName(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getSchemaName(i);
                             }
                         },
                        useKeys("getSchemaName", i));
    }

    @Override
    public int getPrecision(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? 0 :
                                          realResultSetMetaData.getPrecision(i);
                              }
                          },
                        useKeys("getPrecision", i));
    }

    @Override
    public int getScale(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? 0 :
                                          realResultSetMetaData.getScale(i);
                              }
                          },
                        useKeys("getScale", i));
    }

    @Override
    public String getTableName(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getTableName(i);
                             }
                         },
                        useKeys("getTableName", i));
    }

    @Override
    public String getCatalogName(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getCatalogName(i);
                             }
                         },
                        useKeys("getCatalogName", i));
    }

    @Override
    public int getColumnType(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? 0 :
                                          realResultSetMetaData.getColumnType(i);
                              }
                          },
                        useKeys("getColumnType", i));
    }

    @Override
    public String getColumnTypeName(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getColumnTypeName(i);
                             }
                         },
                        useKeys("getColumnTypeName", i));
    }

    @Override
    public boolean isReadOnly(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isReadOnly(i);
                              }
                          },
                        useKeys("isReadOnly", i));
    }

    @Override
    public boolean isWritable(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isWritable(i);
                              }
                          },
                        useKeys("isWritable", i));
    }

    @Override
    public boolean isDefinitelyWritable(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isDefinitelyWritable(i);
                              }
                          },
                        useKeys("isDefinitelyWritable", i));
    }

    @Override
    public String getColumnClassName(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return realResultSetMetaData == null ? null :
                                         realResultSetMetaData.getColumnClassName(i);
                             }
                         },
                        useKeys("getColumnClassName", i));
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
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
                                  return realResultSetMetaData == null ? false :
                                          realResultSetMetaData.isWrapperFor(aClass);
                              }
                          },
                        id, "isWrapperFor", aClass.getCanonicalName());
    }

    private String[] useKeys(String name, int i) {
        return new String[]{id, name, String.valueOf(i)};
    }
}
