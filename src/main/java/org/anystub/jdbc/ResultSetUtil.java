package org.anystub.jdbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.anystub.ObjectMapperFactory;
import org.h2.tools.SimpleResultSet;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

import static java.sql.Types.BIGINT;
import static java.sql.Types.BLOB;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NCHAR;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.REAL;
import static java.sql.Types.ROWID;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.SQLXML;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARCHAR;

public class ResultSetUtil {

    private static final Logger LOGGER = Logger.getLogger("ResultSetUtil");
    private static final ConcurrentHashMap<String, Function<String, ? extends Object>> adapters= new ConcurrentHashMap<>();

    private ResultSetUtil() {

    }

    public static void registerTypeAdapter(String typeName, Function<String, ? extends Object> adapter) {
        adapters.put(typeName, adapter);
    }

    public static void enableMsSql() {
        registerTypeAdapter("uniqueidentifier", next ->
                next == null || next.isEmpty() ? null : UUID.fromString(next));
    }

    public static List<String> encodeHeader(ResultSetMetaData metaData) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        res.add(Integer.toString(metaData.getColumnCount()));
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnName(i);
            String columnLabel = metaData.getColumnLabel(i);
            if (columnLabel == null || columnLabel.equalsIgnoreCase(columnName)) {
                res.add(columnName);
            } else {
                res.add(String.format("%s/%s", columnName, columnLabel));
            }
            res.add(metaData.getColumnTypeName(i));
            res.add(Integer.toString(metaData.getColumnType(i)));
            res.add(Integer.toString(metaData.getPrecision(i)));
            res.add(Integer.toString(metaData.getScale(i)));
        }

        return res;
    }

    public static List<String> encodeData(ResultSet resultSet, ResultSetMetaData resultSetMetaData) throws SQLException {
        List<String> res = new ArrayList<>();
        while (resultSet.next()) {
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                String s = encodeValue(resultSet, resultSetMetaData.getColumnType(i), i);
                res.add(s);
            }
        }

        return res;
    }

    public static List<String> encode(ResultSet resultSet, ResultSetMetaData resultSetMetaData) {
        List<String> res;
        try {
            res = encodeHeader(resultSetMetaData);
            res.addAll(encodeData(resultSet, resultSetMetaData));
        } catch (SQLException e) {
            throw new NoSuchElementException(e.getMessage());
        }
        return res;
    }

    public static List<String> encode(ResultSet resultSet) {
        try {
            return encode(resultSet, resultSet.getMetaData());
        } catch (SQLException e) {
            throw new UnsupportedOperationException("failed extract ResultSetMetaData");
        }
    }

    public static List<String> encode(PreparedStatement preparedStatement, ResultSet resultSet) {
        List<String> encode;
        ResultSetMetaData metaData;
        try {
            metaData = preparedStatement.getMetaData();
            encode = ResultSetUtil.encode(resultSet, metaData);
        } catch (SQLException e) {
            encode = encode(resultSet);
        }
        return encode;
    }

    public static SimpleResultSet decode(Iterable<String> values) {
        // * support aliases in SimpleResultSet it creates 2nd column, and put copy of the data
        final SimpleResultSet simpleResultSet = new SimpleResultSet();
        // * keeps column numbers, to double values
        final Set<Integer> doubleColumnIndexes = new HashSet<>();
        final List<Integer> decodedValueTypes = new ArrayList<>();
        final List<String> decodedValueTypeNames = new ArrayList<>();

        Iterator<String> it = values.iterator();
        if (it.hasNext()) {
            int columnCount = Integer.parseInt(it.next());
            for (int i = 0; i < columnCount; i++) {
                String cNameLabel = it.next();
                String cTypeName = it.next();
                int cType = Integer.parseInt(it.next());
                int cPrecision = Integer.parseInt(it.next());
                int cScale = Integer.parseInt(it.next());

                decodedValueTypes.add(cType);
                decodedValueTypeNames.add(cTypeName);
                String[] split = cNameLabel.split("/");

                if (split.length == 2 && split[0] != null && !split[0].equalsIgnoreCase(split[1])) {
                    simpleResultSet.addColumn(split[0], cType, cTypeName, cPrecision, cScale);
                    simpleResultSet.addColumn(split[1], cType, cTypeName, cPrecision, cScale);
                    doubleColumnIndexes.add(i);
                } else {
                    simpleResultSet.addColumn(cNameLabel, cType, cTypeName, cPrecision, cScale);
                }
            }
            while (it.hasNext() && columnCount > 0) {
                ArrayList<Object> row = new ArrayList<>();
                for (int i = 0; i < columnCount; i++) {
                    String next = it.next();
                    Object item = null;
                    Function<String, ?> stringFunction = adapters.get(decodedValueTypeNames.get(i));
                    if (stringFunction != null) {
                        item = stringFunction.apply(next);
                    }
                    if (item == null) {
                        item = decodeValue(next, decodedValueTypes.get(i));
                    }

                    row.add(item);
                    if (doubleColumnIndexes.contains(i)) {
                        row.add(item);
                    }
                }
                simpleResultSet.addRow((Object[]) row.toArray(new Object[0]));
            }
        }
        return simpleResultSet;
    }

    private static String encodeValue(ResultSet resultSet, int columnType, int column) {
        try {
            Object value = resultSet.getObject(column);
            if (resultSet.wasNull()) {
                return null;
            }

            if (columnType == CHAR ||
                    columnType == VARCHAR ||
                    columnType == LONGVARCHAR) {
                return value.toString();
            }

            switch (columnType) {
//            case  BIT = -7;
//                case TINYINT:
//                case SMALLINT:
//                case INTEGER:
//                case BIGINT:
//                case FLOAT:
//                case NUMERIC:
//                case DECIMAL:
//                case REAL:
//                case DOUBLE:
                case CHAR:
                case VARCHAR:
                case LONGVARCHAR:
                case NCHAR:
                    return resultSet.getString(column);
//                case DATE:
//                case TIME:
//                case TIMESTAMP:
//            case  BINARY = -2;
//            case  VARBINARY = -3;
//            case  LONGVARBINARY = -4;
//            case  NULL = 0;
//            case  OTHER = 1111;
//            case  JAVA_OBJECT = 2000;
//            case  DISTINCT = 2001;
//            case  STRUCT = 2002;
//            case  ARRAY = 2003;
                case BLOB:
                    return SqlTypeEncoder.encodeBlob(resultSet.getBlob(column));
                case CLOB:
                    return SqlTypeEncoder.encodeClob(resultSet.getClob(column));
//            case  REF = 2006;
//            case  DATALINK = 70;
//                case BOOLEAN:
                case ROWID:
                    return SqlTypeEncoder.encodeRowid(resultSet.getRowId(column));
//            case  NVARCHAR = -9;
//            case  LONGNVARCHAR = -16;
//            case  NCLOB = 2011;
                case SQLXML:
                    return SqlTypeEncoder.encodeSQLXML(resultSet.getSQLXML(column));
//            case  REF_CURSOR = 2012;
//            case  TIME_WITH_TIMEZONE = 2013;
//            case  TIMESTAMP_WITH_TIMEZONE = 2014;
                default:
            }

            return ObjectMapperFactory.get().writeValueAsString(value);
        } catch (SQLException | JsonProcessingException e) {
            LOGGER.severe(() -> String.format("Failed encode value on %d of type %d", column, columnType));
            return "";
        }
    }

    private static <R> R decodeValue(String v, Class<R> returnType, R def) {
        try {
            final ObjectMapper objectMapper = ObjectMapperFactory.get();
            return objectMapper.readValue(v, returnType);
        } catch (JsonProcessingException | RuntimeException e) {
            LOGGER.severe(() -> String.format("Failed decode value %s into %s", v, returnType.getName()));
            return def;
        }
    }

    public static Object decodeValue(String next, int columnType) {
        if (next == null) {
            return null;
        }
        switch (columnType) {
//            case  BIT = -7;
            case TINYINT:
            case SMALLINT:
                return decodeValue(next, Short.class, (short)0);
            case INTEGER:
                return decodeValue(next, Integer.class, 0);
            case BIGINT:
                return decodeValue(next, Long.class, 0L);
            case FLOAT:
                return decodeValue(next, Float.class, 0f);
            case NUMERIC:
            case DECIMAL:
            case REAL:
            case DOUBLE:
                return decodeValue(next, Double.class, 0.0);
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
                return next;
            case DATE:
                return decodeValue(next, Date.class, Date.valueOf(LocalDate.MIN));
            case TIME:
                return decodeValue(next, Time.class, Time.valueOf(LocalTime.MIN));
            case TIMESTAMP:
                return decodeValue(next, Timestamp.class, Timestamp.valueOf(LocalDateTime.MIN));
//            case  BINARY:// = -2;
//                return decodeValue(next, UUID.class, UUID.fromString("00000000-0000-0000-0000-000000000000"));
//            case  VARBINARY = -3;
//            case  LONGVARBINARY = -4;
//            case  NULL = 0;
//            case  OTHER = 1111;
//            case  JAVA_OBJECT = 2000;
//            case  DISTINCT = 2001;
//            case  STRUCT = 2002;
//            case  ARRAY = 2003;
            case BLOB:
//                return decodeValue(next, Blob.class, null);
                return SqlTypeEncoder.decodeBlob(next);
            case CLOB:
                return SqlTypeEncoder.decodeClob(next);
//            case  REF = 2006;
//            case  DATALINK = 70;
            case BOOLEAN:
                return decodeValue(next, Boolean.class, false);
            case ROWID:
                return SqlTypeEncoder.decodeRowid(next);
            case NCHAR:
                return next.length()>0 ? next.charAt(0): '\0';
//            case  NVARCHAR = -9;
//            case  LONGNVARCHAR = -16;
//            case  NCLOB = 2011;
            case SQLXML:
                return SqlTypeEncoder.decodeSQLXML(next);
//            case  REF_CURSOR = 2012;
//            case  TIME_WITH_TIMEZONE = 2013;
//            case  TIMESTAMP_WITH_TIMEZONE = 2014;
            default:
                break;
        }
        return next;
    }

}
