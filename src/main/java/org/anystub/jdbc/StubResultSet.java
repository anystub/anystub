package org.anystub.jdbc;

import org.anystub.DecoderSimple;
import org.anystub.EncoderSimple;
import org.anystub.StringUtil;
import org.anystub.Supplier;

import javax.sql.rowset.serial.SerialRef;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class StubResultSet implements ResultSet {
    private final StubConnection stubConnection;
    private final String[] statementId;

    private ResultSet realResultSet = null;

    public StubResultSet(StubConnection stubConnection, String[] statementId, Supplier<ResultSet, SQLException> resultSetSQLExceptionSupplier) throws SQLException {
        this.stubConnection = stubConnection;
        this.statementId = statementId;
        stubConnection.add(() -> realResultSet = resultSetSQLExceptionSupplier.get());
    }


    @Override
    public boolean next() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().next();
                              }
                          },
                        callKey("next"));
    }

    @Override
    public void close() throws SQLException {
        if (getRealResultSet() != null) {
            stubConnection.runSql();
            getRealResultSet().close();
        }
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
                                  return getRealResultSet().wasNull();
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
                                 return getRealResultSet().getString(i);
                             }
                         },
                        callKey("getString", i));
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
                                  return getRealResultSet().getBoolean(i);
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
                                  return getRealResultSet().getInt(i);
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
                                  return getRealResultSet().getInt(i);
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
                                  return getRealResultSet().getInt(i);
                              }
                          },
                        callKey("getInt", i));
    }

    @Override
    public long getLong(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Long, SQLException>() {
                             @Override
                             public Long get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getLong(i);
                             }
                         },
                        Long::parseLong,
                        Object::toString,
                        callKey("getLong", i));
    }

    @Override
    public float getFloat(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Float, SQLException>() {
                             @Override
                             public Float get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getFloat(i);
                             }
                         },
                        Float::parseFloat,
                        Object::toString,
                        callKey("getFloat", i));
    }

    @Override
    public double getDouble(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Double, SQLException>() {
                             @Override
                             public Double get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getDouble(i);
                             }
                         },
                        Double::parseDouble,
                        Object::toString,
                        callKey("getDouble", i));
    }

    @Override
    public BigDecimal getBigDecimal(int i, int i1) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<BigDecimal, SQLException>() {
                             @Override
                             public BigDecimal get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBigDecimal(i, i1);
                             }
                         },
                        BigDecimal::new,
                        BigDecimal::toString,
                        callKey("getBigDecimal", i, i1));
    }

    @Override
    public byte[] getBytes(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<byte[], SQLException>() {
                             @Override
                             public byte[] get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBytes(i);
                             }
                         },
                        values -> Base64
                                .getDecoder()
                                .decode(values),
                        bytes -> (Base64
                                .getEncoder()
                                .encodeToString(bytes)),
                        callKey("getBytes", i));
    }

    @Override
    public Date getDate(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Date, SQLException>() {
                             @Override
                             public Date get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getDate(i);
                             }
                         },
                        Date::valueOf,
                        Date::toString,
                        callKey("getDate", i));
    }

    @Override
    public Time getTime(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Time, SQLException>() {
                             @Override
                             public Time get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getTime(i);
                             }
                         },
                        Time::valueOf,
                        Time::toString,
                        callKey("getTime", i));
    }

    @Override
    public Timestamp getTimestamp(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Timestamp, SQLException>() {
                             @Override
                             public Timestamp get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getTimestamp(i);
                             }
                         },
                        Timestamp::valueOf,
                        Timestamp::toString,
                        callKey("getTimestamp", i));
    }

    @Override
    public InputStream getAsciiStream(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<InputStream, SQLException>() {
                             @Override
                             public InputStream get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getAsciiStream(i);
                             }
                         },
                        StringUtil::recoverInputStream,
                        StringUtil::toCharacterString,
                        callKey("getAsciiStream", i));
    }

    @Override
    public InputStream getUnicodeStream(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<InputStream, SQLException>() {
                             @Override
                             public InputStream get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getUnicodeStream(i);
                             }
                         },
                        StringUtil::recoverInputStream,
                        StringUtil::toCharacterString,
                        callKey("getUnicodeStream", i));
    }

    @Override
    public InputStream getBinaryStream(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<InputStream, SQLException>() {
                             @Override
                             public InputStream get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBinaryStream(i);
                             }
                         },
                        StringUtil::recoverInputStream,
                        StringUtil::toCharacterString,
                        callKey("getBinaryStream", i));
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
                                 return getRealResultSet().getString(s);
                             }
                         },
                        callKey("getString", s));
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
                                  return getRealResultSet().getBoolean(s);
                              }
                          },
                        callKey("getBoolean", s));
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
                                  return getRealResultSet().getInt(s);
                              }
                          },
                        callKey("getByte", s)).byteValue();
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
                                  return getRealResultSet().getInt(s);
                              }
                          },
                        callKey("getShort", s)).shortValue();
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
                                  return getRealResultSet().getInt(s);
                              }
                          },
                        callKey("getInt", s));
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
                                  return getRealResultSet().getLong(s);
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
                                  return getRealResultSet().getFloat(s);
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
                                  return getRealResultSet().getDouble(s);
                              }
                          },
                        values -> Double.parseDouble(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getDouble", s));
    }

    @Override
    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request2(new Supplier<BigDecimal, SQLException>() {
                              @Override
                              public BigDecimal get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().getBigDecimal(s, i);
                              }
                          },
                        values -> new BigDecimal(values.iterator().next()),
                        aLong -> singletonList(aLong.toString()),
                        callKey("getBigDecimal", s, i));
    }

    @Override
    public byte[] getBytes(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<byte[], SQLException>() {
                             @Override
                             public byte[] get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBytes(s);
                             }
                         },
                        values -> Base64
                                .getDecoder()
                                .decode(values),
                        bytes -> (Base64
                                .getEncoder()
                                .encodeToString(bytes)),
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
                                 return getRealResultSet().getDate(s);
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
                                 return getRealResultSet().getTime(s);
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
                                 return getRealResultSet().getTimestamp(s);
                             }
                         },
                        Timestamp::valueOf,
                        Timestamp::toString,
                        callKey("getTimestamp", s));
    }

    @Override
    public InputStream getAsciiStream(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<InputStream, SQLException>() {
                             @Override
                             public InputStream get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getAsciiStream(s);
                             }
                         },
                        StringUtil::recoverInputStream,
                        StringUtil::toCharacterString,
                        callKey("getAsciiStream", s));
    }

    @Override
    public InputStream getUnicodeStream(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<InputStream, SQLException>() {
                             @Override
                             public InputStream get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getUnicodeStream(s);
                             }
                         },
                        StringUtil::recoverInputStream,
                        StringUtil::toCharacterString,
                        callKey("getUnicodeStream", s));
    }

    @Override
    public InputStream getBinaryStream(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<InputStream, SQLException>() {
                             @Override
                             public InputStream get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBinaryStream(s);
                             }
                         },
                        StringUtil::recoverInputStream,
                        StringUtil::toCharacterString,
                        callKey("getBinaryStream", s));
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        stubConnection.add(() -> getRealResultSet().clearWarnings());

    }

    @Override
    public String getCursorName() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<String, SQLException>() {
                             @Override
                             public String get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getCursorName();
                             }
                         },
                        callKey("getCursorName"));
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return new StubResultSetMetaData(stubConnection, statementId[0], () -> getRealResultSet().getMetaData());
    }

    @Override
    public Object getObject(int i) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String s) throws SQLException {
        return null;
    }

    @Override
    public int findColumn(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().findColumn(s);
                              }
                          },
                        callKey("findColumn", s));
    }

    @Override
    public Reader getCharacterStream(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Reader, SQLException>() {
                             @Override
                             public Reader get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getCharacterStream(i);
                             }
                         },
                        new DecoderSimple<Reader>() {
                            @Override
                            public Reader decode(String values) {
                                return null;
                            }
                        },
                        new EncoderSimple<Reader>() {
                            @Override
                            public String encode(Reader reader) {
                                return null;
                            }
                        },
                        callKey("getLong", i));
    }

    @Override
    public Reader getCharacterStream(String s) throws SQLException {
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<BigDecimal, SQLException>() {
                             @Override
                             public BigDecimal get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBigDecimal(i);
                             }
                         },
                        BigDecimal::new,
                        BigDecimal::toString,
                        callKey("getBigDecimal", i));
    }

    @Override
    public BigDecimal getBigDecimal(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<BigDecimal, SQLException>() {
                             @Override
                             public BigDecimal get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getBigDecimal(s);
                             }
                         },
                        BigDecimal::new,
                        BigDecimal::toString,
                        callKey("getBigDecimal", s));
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().isBeforeFirst();
                              }
                          },
                        callKey("isBeforeFirst"));
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().isAfterLast();
                              }
                          },
                        callKey("isAfterLast"));
    }

    @Override
    public boolean isFirst() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().isFirst();
                              }
                          },
                        callKey("isFirst"));
    }

    @Override
    public boolean isLast() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().isLast();
                              }
                          },
                        callKey("isLast"));
    }

    @Override
    public void beforeFirst() throws SQLException {
        stubConnection.add(() -> getRealResultSet().beforeFirst());
    }

    @Override
    public void afterLast() throws SQLException {
        stubConnection.add(() -> getRealResultSet().afterLast());

    }

    @Override
    public boolean first() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().first();
                              }
                          },
                        callKey("first"));
    }

    @Override
    public boolean last() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(new Supplier<Boolean, SQLException>() {
                              @Override
                              public Boolean get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().last();
                              }
                          },
                        callKey("last"));
    }

    @Override
    public int getRow() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().getRow();
                              }
                          },
                        callKey("getRow"));
    }

    @Override
    public boolean absolute(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() ->getRealResultSet().absolute(i)),
                        callKey("absolute", i));
    }

    @Override
    public boolean relative(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() -> getRealResultSet().relative(i)),
                        callKey("relative", i));
    }

    @Override
    public boolean previous() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() -> getRealResultSet().previous()),
                        callKey("previous"));
    }

    @Override
    public void setFetchDirection(int i) throws SQLException {
        stubConnection.add(() -> getRealResultSet().setFetchDirection(i));
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(supplier(() -> getRealResultSet().getFetchDirection()),
                        callKey("getFetchDirection"));
    }

    @Override
    public void setFetchSize(int i) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().setFetchSize(i));
    }

    @Override
    public int getFetchSize() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(supplier(() -> getRealResultSet().getFetchSize()),
                        callKey("getFetchSize"));
    }

    @Override
    public int getType() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(supplier(() -> getRealResultSet().getType()),
                        callKey("getType"));
    }

    @Override
    public int getConcurrency() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(supplier(() -> getRealResultSet().getConcurrency()),
                        callKey("getConcurrency"));
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() -> getRealResultSet().rowUpdated()),
                        callKey("rowUpdated"));
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() -> getRealResultSet().rowInserted()),
                        callKey("rowInserted"));
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() -> getRealResultSet().rowDeleted()),
                        callKey("rowDeleted"));
    }


    @Override
    public void updateNull(int i) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNull(i));
    }

    @Override
    public void updateBoolean(int i, boolean b) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBoolean(i, b));
    }

    @Override
    public void updateByte(int i, byte b) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateByte(i, b));
    }

    @Override
    public void updateShort(int i, short i1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateShort(i, i1));
    }

    @Override
    public void updateInt(int i, int i1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateInt(i, i1));
    }

    @Override
    public void updateLong(int i, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateLong(i, l));
    }

    @Override
    public void updateFloat(int i, float v) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateFloat(i, v));
    }

    @Override
    public void updateDouble(int i, double v) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateDouble(i, v));
    }

    @Override
    public void updateBigDecimal(int i, BigDecimal bigDecimal) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBigDecimal(i, bigDecimal));
    }

    @Override
    public void updateString(int i, String s) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateString(i, s));
    }

    @Override
    public void updateBytes(int i, byte[] bytes) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBytes(i, bytes));
    }

    @Override
    public void updateDate(int i, Date date) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateDate(i, date));
    }

    @Override
    public void updateTime(int i, Time time) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateTime(i, time));
    }

    @Override
    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateTimestamp(i, timestamp));
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, int i1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateAsciiStream(i, inputStream, i1));
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, int i1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBinaryStream(i, inputStream, i1));
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, int i1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateCharacterStream(i, reader, i1));
    }

    @Override
    public void updateObject(int i, Object o, int i1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateObject(i, o, i1));
    }

    @Override
    public void updateObject(int i, Object o) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateObject(i, o));
    }

    @Override
    public void updateNull(String s) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateNull(s));
    }

    @Override
    public void updateBoolean(String s, boolean b) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateBoolean(s, b));
    }

    @Override
    public void updateByte(String s, byte b) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateByte(s, b));
    }

    @Override
    public void updateShort(String s, short i) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateShort(s, i));
    }

    @Override
    public void updateInt(String s, int i) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateInt(s, i));
    }

    @Override
    public void updateLong(String s, long l) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateLong(s, l));
    }

    @Override
    public void updateFloat(String s, float v) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateFloat(s, v));
    }

    @Override
    public void updateDouble(String s, double v) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateDouble(s, v));
    }

    @Override
    public void updateBigDecimal(String s, BigDecimal bigDecimal) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateBigDecimal(s, bigDecimal));
    }

    @Override
    public void updateString(String s, String s1) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateString(s, s1));
    }

    @Override
    public void updateBytes(String s, byte[] bytes) throws SQLException {
        stubConnection.add(() ->
                getRealResultSet().updateBytes(s, bytes));
    }

    @Override
    public void updateDate(String s, Date date) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateDate(s, date));
    }

    @Override
    public void updateTime(String s, Time time) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateTime(s, time));
    }

    @Override
    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateTimestamp(s, timestamp));
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, int i) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateAsciiStream(s, inputStream, i));
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, int i) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBinaryStream(s, inputStream, i));
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateCharacterStream(s, reader, i));
    }

    @Override
    public void updateObject(String s, Object o, int i) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateObject(s, o, i));
    }

    @Override
    public void updateObject(String s, Object o) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateObject(s, o));
    }

    @Override
    public void insertRow() throws SQLException {
        stubConnection.add(() -> getRealResultSet().insertRow());
    }

    @Override
    public void updateRow() throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateRow());
    }

    @Override
    public void deleteRow() throws SQLException {
        stubConnection.add(() -> getRealResultSet().deleteRow());
    }

    @Override
    public void refreshRow() throws SQLException {
        stubConnection.add(() -> getRealResultSet().refreshRow());
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        stubConnection.add(() -> getRealResultSet().cancelRowUpdates());
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        stubConnection.add(() -> getRealResultSet().moveToInsertRow());
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        stubConnection.add(() -> getRealResultSet().moveToCurrentRow());
    }

    @Override
    public Statement getStatement() throws SQLException {
        return new StubStatement(stubConnection);
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        return null;
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
                        Ref r = getRealResultSet().getRef(i);
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
                                 return getRealResultSet().getBlob(i);
                             }
                         },
                        SqlTypeEncoder::decodeBlob,
                        SqlTypeEncoder::encodeBlob,
                        callKey("getBlob", i));
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Clob, SQLException>() {
                             @Override
                             public Clob get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getClob(i);
                             }
                         },
                        SqlTypeEncoder::decodeClob,
                        SqlTypeEncoder::encodeClob,
                        callKey("getClob", i));
    }

    @Override
    public Array getArray(int i) throws SQLException {
        return null;
    }

    @Override
    public Object getObject(String s, Map<String, Class<?>> map) throws SQLException {
        return null;
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
                        Ref r = getRealResultSet().getRef(s);
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
                                 return getRealResultSet().getBlob(s);
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
                                 return getRealResultSet().getClob(s);
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
    public Date getDate(int i, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Date, SQLException>() {
                             @Override
                             public Date get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getDate(i, calendar);
                             }
                         },
                        Date::valueOf,
                        Date::toString,
                        callKey("getDate", i));
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
                                 return getRealResultSet().getDate(s, calendar);
                             }
                         },
                        Date::valueOf,
                        Date::toString,
                        callKey("getDate", s));
    }

    @Override
    public Time getTime(int i, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Time, SQLException>() {
                             @Override
                             public Time get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getTime(i, calendar);
                             }
                         },
                        Time::valueOf,
                        Time::toString,
                        callKey("getTime", i));
    }

    @Override
    public Time getTime(String s, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Time, SQLException>() {
                             @Override
                             public Time get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getTime(s, calendar);
                             }
                         },
                        Time::valueOf,
                        Time::toString,
                        callKey("getTime", s));
    }

    @Override
    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Timestamp, SQLException>() {
                             @Override
                             public Timestamp get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getTimestamp(i, calendar);
                             }
                         },
                        Timestamp::valueOf,
                        Timestamp::toString,
                        callKey("getTimestamp", i));
    }

    @Override
    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<Timestamp, SQLException>() {
                             @Override
                             public Timestamp get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getTimestamp(s, calendar);
                             }
                         },
                        Timestamp::valueOf,
                        Timestamp::toString,
                        callKey("getTimestamp", s));
    }

    @Override
    public URL getURL(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<URL, SQLException>() {
                             @Override
                             public URL get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getURL(i);
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
                        callKey("getURL", i));
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
                                 return getRealResultSet().getURL(s);
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
    public void updateRef(int i, Ref ref) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateRef(i, ref));
    }

    @Override
    public void updateRef(String s, Ref ref) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateRef(s, ref));
    }

    @Override
    public void updateBlob(int i, Blob blob) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBlob(i, blob));
    }

    @Override
    public void updateBlob(String s, Blob blob) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBlob(s, blob));
    }

    @Override
    public void updateClob(int i, Clob clob) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(i, clob));
    }

    @Override
    public void updateClob(String s, Clob clob) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(s, clob));
    }

    @Override
    public void updateArray(int i, Array array) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateArray(i, array));
    }

    @Override
    public void updateArray(String s, Array array) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateArray(s, array));
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
                                 return getRealResultSet().getRowId(i);
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
                                 return getRealResultSet().getRowId(s);
                             }
                         },
                        SqlTypeEncoder::decodeRowid,
                        SqlTypeEncoder::encodeRowid,
                        callKey("getRowId", s));
    }

    @Override
    public void updateRowId(int i, RowId rowId) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateRowId(i, rowId));
    }

    @Override
    public void updateRowId(String s, RowId rowId) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateRowId(s, rowId));
    }

    @Override
    public int getHoldability() throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestI(new Supplier<Integer, SQLException>() {
                              @Override
                              public Integer get() throws SQLException {
                                  stubConnection.runSql();
                                  return getRealResultSet().getHoldability();
                              }
                          },
                        callKey("getHoldability"));
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
                                  return getRealResultSet().isClosed();
                              }
                          },
                        callKey("isClosed"));
    }

    @Override
    public void updateNString(int i, String s) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNString(i, s));
    }

    @Override
    public void updateNString(String s, String s1) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNString(s, s1));
    }

    @Override
    public void updateNClob(int i, NClob nClob) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNClob(i, nClob));
    }

    @Override
    public void updateNClob(String s, NClob nClob) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNClob(s, nClob));
    }

    @Override
    public NClob getNClob(int i) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<NClob, SQLException>() {
                             @Override
                             public NClob get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getNClob(i);
                             }
                         },
                        SqlTypeEncoder::decodeNClob,
                        SqlTypeEncoder::encodeNClob,
                        callKey("getNClob", i));
    }

    @Override
    public NClob getNClob(String s) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .request(new Supplier<NClob, SQLException>() {
                             @Override
                             public NClob get() throws SQLException {
                                 stubConnection.runSql();
                                 return getRealResultSet().getNClob(s);
                             }
                         },
                        SqlTypeEncoder::decodeNClob,
                        SqlTypeEncoder::encodeNClob,
                        callKey("getNClob", s));
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
                                 return getRealResultSet().getSQLXML(i);
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
                                 return getRealResultSet().getSQLXML(s);
                             }
                         },
                        SqlTypeEncoder::decodeSQLXML,
                        SqlTypeEncoder::encodeSQLXML,
                        callKey("getSQLXML", s));
    }

    @Override
    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateSQLXML(i, sqlxml));
    }

    @Override
    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateSQLXML(s, sqlxml));
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
                        return getRealResultSet().getNString(i);
                    }
                }, callKey("getNString", i));
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
                        return getRealResultSet().getNString(s);
                    }
                }, callKey("getNString", s));
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
    public void updateNCharacterStream(int i, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNCharacterStream(i, reader, l));
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNCharacterStream(s, reader, l));
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateAsciiStream(i, inputStream, l));
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBinaryStream(i, inputStream, l));
    }

    @Override
    public void updateCharacterStream(int i, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateCharacterStream(i, reader, l));
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateAsciiStream(s, inputStream, l));
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBinaryStream(s, inputStream, l));
    }

    @Override
    public void updateCharacterStream(String s, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateCharacterStream(s, reader, l));
    }

    @Override
    public void updateBlob(int i, InputStream inputStream, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBlob(i, inputStream, l));
    }

    @Override
    public void updateBlob(String s, InputStream inputStream, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBlob(s, inputStream, l));
    }

    @Override
    public void updateClob(int i, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(i, reader, l));
    }

    @Override
    public void updateClob(String s, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(s, reader, l));
    }

    @Override
    public void updateNClob(int i, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(i, reader, l));
    }

    @Override
    public void updateNClob(String s, Reader reader, long l) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNClob(s, reader, l));
    }

    @Override
    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNCharacterStream(i, reader));
    }

    @Override
    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNCharacterStream(s, reader));
    }

    @Override
    public void updateAsciiStream(int i, InputStream inputStream) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateAsciiStream(i, inputStream));
    }

    @Override
    public void updateBinaryStream(int i, InputStream inputStream) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBinaryStream(i, inputStream));
    }

    @Override
    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateCharacterStream(i, reader));
    }

    @Override
    public void updateAsciiStream(String s, InputStream inputStream) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateAsciiStream(s, inputStream));
    }

    @Override
    public void updateBinaryStream(String s, InputStream inputStream) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBinaryStream(s, inputStream));
    }

    @Override
    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateCharacterStream(s, reader));
    }

    @Override
    public void updateBlob(int i, InputStream inputStream) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBlob(i, inputStream));
    }

    @Override
    public void updateBlob(String s, InputStream inputStream) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateBlob(s, inputStream));
    }

    @Override
    public void updateClob(int i, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(i, reader));
    }

    @Override
    public void updateClob(String s, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateClob(s, reader));
    }

    @Override
    public void updateNClob(int i, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNClob(i, reader));
    }

    @Override
    public void updateNClob(String s, Reader reader) throws SQLException {
        stubConnection.add(() -> getRealResultSet().updateNClob(s, reader));
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
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return stubConnection
                .getStubDataSource()
                .getBase()
                .requestB(supplier(() -> getRealResultSet().isWrapperFor(aClass)),
                        callKey("isWrapperFor", aClass.getCanonicalName()));
    }


    public ResultSet getRealResultSet() {
        return realResultSet;
    }


    protected String[] callKey(String callName, Object... a) {

        List<String> callKey = new ArrayList<>();
        callKey.add("resultSet");
        callKey.addAll(asList((statementId)));
        callKey.add(callName);
        callKey.addAll(Arrays.stream(a)
                .map(String::valueOf)
                .collect(Collectors.toList()));

        return stubConnection.callKey(callKey.toArray(new String[0]));
    }


    private <T> Supplier<T, SQLException> supplier(Supplier<T, SQLException> s) {
        return () -> {
            stubConnection.runSql();
            return s.get();
        };
    }

}
