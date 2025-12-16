package org.anystub.jdbc;

import org.anystub.Supplier;

import javax.sql.rowset.serial.SerialRef;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Calendar;
import java.util.Map;
import java.util.NoSuchElementException;

import static java.util.Collections.singletonList;

public class StubCallableStatement extends StubPreparedStatement implements CallableStatement {
    private CallableStatement realCallableStatement = null;

    public StubCallableStatement(StubConnection stubConnection, String sql) throws SQLException {
        super(true, stubConnection, sql);

        stubConnection.add(() -> {
            realCallableStatement = stubConnection.getRealConnection().prepareCall(sql);
        });
    }

    public StubCallableStatement(StubConnection stubConnection, String sql, int i, int i1) throws SQLException {
        super(stubConnection, sql);

        stubConnection.add(() -> {
            realCallableStatement = stubConnection.getRealConnection().prepareCall(sql, i, i1);
        });
    }

    public StubCallableStatement(StubConnection stubConnection, String sql, int i, int i1, int i2) throws SQLException {
        super(stubConnection, sql);

        stubConnection.add(() -> {
            realCallableStatement = stubConnection.getRealConnection().prepareCall(sql, i, i1, i2);
        });
    }


    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        stubConnection.add(() -> {
            realCallableStatement.registerOutParameter(parameterIndex, sqlType);
        });

    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        stubConnection.add(() -> {
            realCallableStatement.registerOutParameter(parameterIndex, sqlType, scale);
        });

    }

    @Override
    public boolean wasNull() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().wasNull();
                              }
                          },
                        callKey("wasNull"));
    }

    @Override
    public String getString(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getString(i);
                             }
                         },
                        callKey("getString", 1));
    }

    @Override
    public boolean getBoolean(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getBoolean(i);
                              }
                          },
                        callKey("getBoolean", i));
    }

    @Override
    public byte getByte(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return (int) realCallableStatement.getByte(i);
                              }
                          },
                        callKey("getByte", i)).byteValue();
    }

    @Override
    public short getShort(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return (int) realCallableStatement.getShort(i);
                              }
                          },
                        callKey("getShort", i)).shortValue();
    }

    @Override
    public int getInt(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getInt(i);
                              }
                          },
                        callKey("getInt", i));
    }

    @Override
    public long getLong(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Long, SQLException>() {
                              @Override
                              public Long get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getLong(i);
                              }
                          },
                        values -> Long.parseLong(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getLong", i));
    }

    @Override
    public float getFloat(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Float, SQLException>() {
                              @Override
                              public Float get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getFloat(i);
                              }
                          },
                        values -> Float.parseFloat(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getFloat", i));
    }

    @Override
    public double getDouble(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Double, SQLException>() {
                              @Override
                              public Double get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getDouble(i);
                              }
                          },
                        values -> Double.parseDouble(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getDouble", i));
    }

    @Deprecated
    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<BigDecimal, SQLException>() {
                              @Override
                              public BigDecimal get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getBigDecimal(i, i1);
                              }
                          },
                        values -> new BigDecimal(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getBigDecimal", i, i1));
    }

    @Override
    public byte[] getBytes(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<byte[], SQLException>() {
                              @Override
                              public byte[] get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getBytes(i);
                              }
                          },
                        values -> Base64
                                .getDecoder()
                                .decode(values.iterator().next()),
                        bytes -> {
                            return singletonList(Base64
                                    .getEncoder()
                                    .encodeToString(bytes));
                        },
                        callKey("getBytes", i));
    }

    @Override
    public Date getDate(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Date, SQLException>() {
                              @Override
                              public Date get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getDate(i);
                              }
                          },
                        values -> Date.valueOf(values.iterator().next()),
                        date -> singletonList(date.toString()),
                        callKey("getDate", i));
    }

    @Override
    public Time getTime(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Time, SQLException>() {
                              @Override
                              public Time get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getTime(i);
                              }
                          },
                        values -> Time.valueOf(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getTime", i));
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Timestamp, SQLException>() {
                              @Override
                              public Timestamp get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getTimestamp(i);
                              }
                          },
                        values -> Timestamp.valueOf(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getTimestamp", i));
    }

    @Override
    public Object getObject(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestSerializable(new Supplier<Serializable, SQLException>() {
                    @Override
                    public Serializable get() throws SQLException {
                        stubConnection.runSql();
                        Object object = getRealStatement().getObject(i);
                        if (object instanceof Serializable) {
                            return (Serializable) object;
                        }
                        throw new UnsupportedOperationException();
                    }
                }, callKey("getObject", i));
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<BigDecimal, SQLException>() {
                              @Override
                              public BigDecimal get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getBigDecimal(i);
                              }
                          },
                        values -> new BigDecimal(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getBigDecimal", i));
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestSerializable(new Supplier<Serializable, SQLException>() {
                    @Override
                    public Serializable get() throws SQLException {
                        stubConnection.runSql();
                        Object object = getRealStatement().getObject(i, map);
                        if (object instanceof Serializable) {
                            return (Serializable) object;
                        }
                        throw new UnsupportedOperationException();
                    }
                }, callKey("getObject", i));
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestSerializable(new Supplier<SerialRef, SQLException>() {
                    @Override
                    public SerialRef get() throws SQLException {
                        stubConnection.runSql();
                        Ref r = getRealStatement().getRef(i);
                        return new SerialRef(r);
                    }
                }, callKey("getRef", i));
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Blob, SQLException>() {
                             @Override
                             public Blob get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getBlob(i);
                             }
                         },
                        SqlTypeEncoder::decodeBlob,
                        SqlTypeEncoder::encodeBlob,
                        callKey("getBlob", i));
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        return null;
    }

    @Override
    public Array getArray(int i) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(int i, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Date, SQLException>() {
                              @Override
                              public Date get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getDate(i, calendar);
                              }
                          },
                        values -> Date.valueOf(values.iterator().next()),
                        date -> singletonList(date.toString()),
                        callKey("getDate", i));
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Time, SQLException>() {
                              @Override
                              public Time get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getTime(i, calendar);
                              }
                          },
                        values -> Time.valueOf(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getTime", i));
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Timestamp, SQLException>() {
                              @Override
                              public Timestamp get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getTimestamp(i, calendar);
                              }
                          },
                        values -> Timestamp.valueOf(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getTimestamp", i));
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().registerOutParameter(parameterIndex, sqlType, typeName);
        });
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().registerOutParameter(parameterName, sqlType);
        });
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().registerOutParameter(parameterName, sqlType, scale);
        });
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        stubConnection.add(() -> {
            getRealStatement().registerOutParameter(parameterName, sqlType, typeName);
        });
    }

    @Override
    public URL getURL(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<URL, SQLException>() {
                              @Override
                              public URL get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getURL(i);
                              }
                          },
                        values -> {
                            try {
                                return new URL(values.iterator().next());
                            } catch (MalformedURLException e) {
                                throw new NoSuchElementException(e.getMessage());
                            }
                        },
                        aLong -> singletonList(aLong.toString()),
                        callKey("getURL", i));
    }

    @Override
    public void setURL(String s, URL url) throws SQLException {
        addCallKeys(s, url.toString());
        stubConnection.add(() -> {
            getRealStatement().setURL(s, url);
        });
    }

    @Override
    public void setNull(String s, int i) throws SQLException {
        addCallKeys(s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setNull(s, i);
        });
    }

    @Override
    public void setBoolean(String s, boolean b) throws SQLException {
        addCallKeys(s, String.valueOf(b));
        stubConnection.add(() -> {
            realCallableStatement.setBoolean(s, b);
        });
    }

    @Override
    public void setByte(String s, byte b) throws SQLException {
        addCallKeys(s, String.valueOf(b));
        stubConnection.add(() -> {
            realCallableStatement.setByte(s, b);
        });
    }

    @Override
    public void setShort(String s, short i) throws SQLException {
        addCallKeys(s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setShort(s, i);
        });
    }

    @Override
    public void setInt(String s, int i) throws SQLException {
        addCallKeys(s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setInt(s, i);
        });
    }

    @Override
    public void setLong(String s, long l) throws SQLException {
        addCallKeys(s, String.valueOf(l));
        stubConnection.add(() -> {
            realCallableStatement.setLong(s, l);
        });
    }

    @Override
    public void setFloat(String s, float v) throws SQLException {
        addCallKeys(s, String.valueOf(v));
        stubConnection.add(() -> {
            realCallableStatement.setFloat(s, v);
        });
    }

    @Override
    public void setDouble(String s, double v) throws SQLException {
        addCallKeys(s, String.valueOf(v));
        stubConnection.add(() -> {
            realCallableStatement.setDouble(s, v);
        });
    }

    @Override
    public void setBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        addCallKeys(s, bigDecimal.toPlainString());
        stubConnection.add(() -> {
            realCallableStatement.setBigDecimal(s, bigDecimal);
        });
    }

    @Override
    public void setString(String parameterName, String s1) throws SQLException {
        addCallKeys(parameterName, s1);
        stubConnection.add(() -> {
            realCallableStatement.setString(parameterName, s1);
        });
    }

    @Override
    public void setBytes(String s, byte[] bytes) throws SQLException {
        addCallKeys(s, Base64.getEncoder().encodeToString(bytes));
        stubConnection.add(() -> {
            realCallableStatement.setBytes(s, bytes);
        });
    }

    @Override
    public void setDate(String s, Date date) throws SQLException {
        addCallKeys(s, date.toString());
        stubConnection.add(() -> {
            realCallableStatement.setDate(s, date);
        });
    }

    @Override
    public void setTime(String s, Time time) throws SQLException {
        addCallKeys(s, time.toString());
        stubConnection.add(() -> {
            realCallableStatement.setTime(s, time);
        });
    }

    @Override
    public void setTimestamp(String s, Timestamp timestamp) throws SQLException {
        addCallKeys(s, timestamp.toString());
        stubConnection.add(() -> {
            realCallableStatement.setTimestamp(s, timestamp);
        });
    }

    @Override
    public void setAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        addCallKeys("setAsciiStream", s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setAsciiStream(s, inputStream, i);
        });

    }

    @Override
    public void setBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        addCallKeys("setBinaryStream", s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setBinaryStream(s, inputStream, i);
        });
    }

    @Override
    public void setObject(String s, Object o, int i, int i1) throws SQLException {
        addCallKeys("setObject", s, String.valueOf(i), String.valueOf(i1));
        stubConnection.add(() -> {
            realCallableStatement.setObject(s, o, i, i1);
        });
    }

    @Override
    public void setObject(String s, Object o, int i) throws SQLException {
        addCallKeys("setObject", s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setObject(s, o, i);
        });
    }

    @Override
    public void setObject(String s, Object o) throws SQLException {
        addCallKeys("setObject", s);
        stubConnection.add(() -> {
            realCallableStatement.setObject(s, o);
        });
    }

    @Override
    public void setCharacterStream(String s, Reader reader, int i) throws SQLException {
        addCallKeys("setCharacterStream", s, String.valueOf(i));
        stubConnection.add(() -> {
            realCallableStatement.setCharacterStream(s, reader, i);
        });
    }

    @Override
    public void setDate(String s, Date date, Calendar calendar) throws SQLException {
        addCallKeys("setDate", s, date.toString());
        stubConnection.add(() -> {
            realCallableStatement.setDate(s, date, calendar);
        });
    }

    @Override
    public void setTime(String s, Time time, Calendar calendar) throws SQLException {
        addCallKeys("setTime", s, time.toString());
        stubConnection.add(() -> {
            realCallableStatement.setTime(s, time, calendar);
        });
    }

    @Override
    public void setTimestamp(String s, Timestamp timestamp, Calendar calendar) throws SQLException {
        addCallKeys("setTimestamp", s, timestamp.toString());
        stubConnection.add(() -> {
            realCallableStatement.setTimestamp(s, timestamp, calendar);
        });
    }

    @Override
    public void setNull(String s, int i, String s1) throws SQLException {
        addCallKeys("setNull", s, String.valueOf(i), s1);
        stubConnection.add(() -> {
            realCallableStatement.setNull(s, i, s1);
        });
    }

    @Override
    public String getString(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                    @Override
                    public String get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getString(s);
                    }
                }, callKey("getString", s));
    }

    @Override
    public boolean getBoolean(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                    @Override
                    public Boolean get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getBoolean(s);
                    }
                }, callKey("getBoolean", s));
    }

    @Override
    public byte getByte(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return (int) getRealStatement().getByte(s);
                    }
                }, callKey("getByte", s))
                .byteValue();
    }

    @Override
    public short getShort(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return (int) getRealStatement().getShort(s);
                    }
                }, callKey("getShort", s)).shortValue();
    }

    @Override
    public int getInt(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                    @Override
                    public Integer get() throws SQLException {
                        stubConnection.runSql();
                        return getRealStatement().getInt(s);
                    }
                }, callKey("getInt", s));
    }

    @Override
    public long getLong(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Long, SQLException>() {
                              @Override
                              public Long get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getLong(s);
                              }
                          },
                        values -> Long.parseLong(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getLong", s));
    }

    @Override
    public float getFloat(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Float, SQLException>() {
                              @Override
                              public Float get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getFloat(s);
                              }
                          },
                        values -> Float.parseFloat(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getFloat", s));
    }

    @Override
    public double getDouble(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Double, SQLException>() {
                              @Override
                              public Double get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getDouble(s);
                              }
                          },
                        values -> Double.parseDouble(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getDouble", s));
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<byte[], SQLException>() {
                              @Override
                              public byte[] get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getBytes(s);
                              }
                          },
                        values -> Base64
                                .getDecoder()
                                .decode(values.iterator().next()),
                        bytes -> {
                            return singletonList(Base64
                                    .getEncoder()
                                    .encodeToString(bytes));
                        },
                        callKey("getBytes", s));
    }

    @Override
    public Date getDate(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Date, SQLException>() {
                             @Override
                             public Date get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getDate(s);
                             }
                         },
                        Date::valueOf,
                        Date::toString,
                        callKey("getDate", s));
    }

    @Override
    public Time getTime(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Time, SQLException>() {
                             @Override
                             public Time get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getTime(s);
                             }
                         },
                        Time::valueOf,
                        Time::toString,
                        callKey("getTime", s));
    }

    @Override
    public Timestamp getTimestamp(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Timestamp, SQLException>() {
                             @Override
                             public Timestamp get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getTimestamp(s);
                             }
                         },
                        Timestamp::valueOf,
                        Timestamp::toString,
                        callKey("getTimestamp", s));
    }

    @Override
    public Object getObject(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestSerializable(new Supplier<Serializable, SQLException>() {
                    @Override
                    public Serializable get() throws SQLException {
                        stubConnection.runSql();
                        Object object = getRealStatement().getObject(s);
                        if (object instanceof Serializable) {
                            return (Serializable) object;
                        }
                        throw new UnsupportedOperationException();
                    }
                }, callKey("getObject", s));
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<BigDecimal, SQLException>() {
                              @Override
                              public BigDecimal get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getBigDecimal(s);
                              }
                          },
                        values -> new BigDecimal(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getBigDecimal", s));
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestSerializable(new Supplier<Serializable, SQLException>() {
                    @Override
                    public Serializable get() throws SQLException {
                        stubConnection.runSql();
                        Object object = getRealStatement().getObject(s, map);
                        if (object instanceof Serializable) {
                            return (Serializable) object;
                        }
                        throw new UnsupportedOperationException();
                    }
                }, callKey("getObject", s));
    }

    @Override
    public Ref getRef(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestSerializable(new Supplier<SerialRef, SQLException>() {
                    @Override
                    public SerialRef get() throws SQLException {
                        stubConnection.runSql();
                        Ref r = getRealStatement().getRef(s);
                        return new SerialRef(r);
                    }
                }, callKey("getRef", s));
    }

    @Override
    public Blob getBlob(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Blob, SQLException>() {
                             @Override
                             public Blob get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getBlob(s);
                             }
                         },
                        SqlTypeEncoder::decodeBlob,
                        SqlTypeEncoder::encodeBlob,
                        callKey("getBlob", s));
    }

    @Override
    public Clob getClob(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Clob, SQLException>() {
                             @Override
                             public Clob get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getClob(s);
                             }
                         },
                        SqlTypeEncoder::decodeClob,
                        SqlTypeEncoder::encodeClob,
                        callKey("getClob", s));
    }

    @Override
    public Array getArray(String s) throws SQLException {
        return null;
    }

    @Override
    public Date getDate(String s, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Date, SQLException>() {
                             @Override
                             public Date get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getDate(s, calendar);
                             }
                         },
                        Date::valueOf,
                        Date::toString,
                        callKey("getDate", s));
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Time, SQLException>() {
                              @Override
                              public Time get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getTime(s, calendar);
                              }
                          },
                        values -> Time.valueOf(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getTime", s));
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<Timestamp, SQLException>() {
                              @Override
                              public Timestamp get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealStatement().getTimestamp(s, calendar);
                              }
                          },
                        values -> Timestamp.valueOf(values.iterator().next()),
                        time -> singletonList(time.toString()),
                        callKey("getTimestamp", s));
    }

    @Override
    public URL getURL(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<URL, SQLException>() {
                             @Override
                             public URL get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getURL(s);
                             }
                         },
                        values -> {
                            try {
                                return new URL(values);
                            } catch (MalformedURLException e) {
                                throw new NoSuchElementException(e.getMessage());
                            }
                        },
                        URL::toString,
                        callKey("getURL", s));
    }

    @Override
    public RowId getRowId(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<RowId, SQLException>() {
                             @Override
                             public RowId get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getRowId(i);
                             }
                         },
                        SqlTypeEncoder::decodeRowid,
                        SqlTypeEncoder::encodeRowid,
                        callKey("getRowId", i));

    }

    @Override
    public RowId getRowId(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<RowId, SQLException>() {
                             @Override
                             public RowId get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getRowId(s);
                             }
                         },
                        SqlTypeEncoder::decodeRowid,
                        SqlTypeEncoder::encodeRowid,
                        callKey("getRowId", s));
    }

    @Override
    public void setRowId(String s, RowId rowId) throws SQLException {
        addCallKeys(s, rowId.toString());
        stubConnection.add(() ->
        {
            getRealStatement().setRowId(s, rowId);
        });
    }

    @Override
    public void setNString(String s, String s1) throws SQLException {
        addCallKeys(s, s1);
        stubConnection.add(() ->
        {
            getRealStatement().setNString(s, s1);
        });
    }

    @Override
    public void setNCharacterStream(String s, Reader reader, long l) throws SQLException {
        addCallKeys("setNCharacterStream", s, String.valueOf(l));
        stubConnection.add(() ->
        {
            getRealStatement().setNCharacterStream(s, reader, l);
        });
    }

    @Override
    public void setNClob(String s, NClob nClob) throws SQLException {
        addCallKeys("setNClob", s);
        stubConnection.add(() ->
        {
            getRealStatement().setNClob(s, nClob);
        });
    }

    @Override
    public void setClob(String s, Reader reader, long l) throws SQLException {
        addCallKeys("setClob", s);
        stubConnection.add(() ->
        {
            getRealStatement().setClob(s, reader, l);
        });
    }

    @Override
    public void setBlob(String s, InputStream inputStream, long l) throws SQLException {
        addCallKeys("setBlob", s, String.valueOf(l));
        stubConnection.add(() ->
        {
            getRealStatement().setBlob(s, inputStream, l);
        });
    }

    @Override
    public void setNClob(String s, Reader reader, long l) throws SQLException {
        addCallKeys("setNClob", s, String.valueOf(l));
        stubConnection.add(() ->
        {
            getRealStatement().setNClob(s, reader, l);
        });
    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        return null;
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSQLXML(String s, SQLXML sqlxml) throws SQLException {
        addCallKeys("setSQLXML", s);
        stubConnection.add(() ->
        {
            getRealStatement().setSQLXML(s, sqlxml);
        });
    }

    @Override
    public SQLXML getSQLXML(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<SQLXML, SQLException>() {
                             @Override
                             public SQLXML get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getSQLXML(i);
                             }
                         },
                        SqlTypeEncoder::decodeSQLXML,
                        SqlTypeEncoder::encodeSQLXML,
                        callKey("getSQLXML", i));
    }

    @Override
    public SQLXML getSQLXML(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<SQLXML, SQLException>() {
                             @Override
                             public SQLXML get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getSQLXML(s);
                             }
                         },
                        SqlTypeEncoder::decodeSQLXML,
                        SqlTypeEncoder::encodeSQLXML,
                        callKey("getSQLXML", s));
    }

    @Override
    public String getNString(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getNString(i);
                             }
                         },
                        callKey("getNString", i));
    }

    @Override
    public String getNString(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealStatement().getNString(s);
                             }
                         },
                        callKey("getNString", s));
    }

    @Override
    public Reader getNCharacterStream(int i) throws SQLException {
        return null;
    }

    @Override
    public Reader getNCharacterStream(String s) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        return null;
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        return null;
    }

    @Override
    public void setBlob(String s, Blob blob) throws SQLException {
        addCallKeys("setBlob", String.valueOf(blob.length()));
        stubConnection.add(() -> {
            getRealStatement().setBlob(s, blob);
        });
    }

    @Override
    public void setClob(String s, Clob clob) throws SQLException {
        addCallKeys("setClob", s, String.valueOf(clob.length()));
        stubConnection.add(() -> {
            getRealStatement().setClob(s, clob);
        });
    }

    @Override
    public void setAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        addCallKeys("setAsciiStream", s, String.valueOf(l));
        stubConnection.add(() -> {
            getRealStatement().setAsciiStream(s, inputStream, l);
        });
    }

    @Override
    public void setBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        addCallKeys("setBinaryStream", s, String.valueOf(l));
        stubConnection.add(() -> {
            getRealStatement().setBinaryStream(s, inputStream, l);
        });
    }

    @Override
    public void setCharacterStream(String s, Reader reader, long l) throws SQLException {
        addCallKeys("setCharacterStream", s, String.valueOf(l));
        stubConnection.add(() -> {
            getRealStatement().setCharacterStream(s, reader, l);
        });
    }

    @Override
    public void setAsciiStream(String s, InputStream inputStream) throws SQLException {
        addCallKeys("setAsciiStream", s);
        stubConnection.add(() -> {
            getRealStatement().setAsciiStream(s, inputStream);
        });
    }

    @Override
    public void setBinaryStream(String s, InputStream inputStream) throws SQLException {
        addCallKeys("setBinaryStream", s);
        stubConnection.add(() -> {
            getRealStatement().setBinaryStream(s, inputStream);
        });
    }

    @Override
    public void setCharacterStream(String s, Reader reader) throws SQLException {
        addCallKeys("setCharacterStream", s);
        stubConnection.add(() -> {
            getRealStatement().setCharacterStream(s, reader);
        });
    }

    @Override
    public void setNCharacterStream(String s, Reader reader) throws SQLException {
        addCallKeys("setNCharacterStream", s);
        stubConnection.add(() -> {
            getRealStatement().setNCharacterStream(s, reader);
        });
    }

    @Override
    public void setClob(String s, Reader reader) throws SQLException {
        addCallKeys("setClob", s);
        stubConnection.add(() -> {
            getRealStatement().setClob(s, reader);
        });
    }

    @Override
    public void setBlob(String s, InputStream inputStream) throws SQLException {
        addCallKeys("setBlob", s);
        stubConnection.add(() -> {
            getRealStatement().setBlob(s, inputStream);
        });
    }

    @Override
    public void setNClob(String s, Reader reader) throws SQLException {
        addCallKeys("setNClob", s);
        stubConnection.add(() -> {
            getRealStatement().setNClob(s, reader);
        });
    }

    @Override
    public <T> T getObject(int i, Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public <T> T getObject(String s, Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement getRealStatement() {
        return realCallableStatement;
    }
}
