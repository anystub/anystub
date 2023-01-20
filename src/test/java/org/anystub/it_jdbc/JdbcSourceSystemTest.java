package org.anystub.it_jdbc;

import org.anystub.AnyStubId;
import org.anystub.RequestMode;
import org.anystub.src.Customer;
import org.h2.tools.SimpleResultSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.sql.Types.VARCHAR;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AnyStubId(filename = "jdbcStub.yml")
public class JdbcSourceSystemTest {

    private Logger log = Logger.getLogger("test");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testInjectedDS() {

        assertEquals(dataSource, jdbcTemplate.getDataSource());
    }

    @Test
    @AnyStubId(filename = "jdbcStub4testSome", requestMode = RequestMode.rmNone)
    public void testSome() {


        log.info("Creating tables");

        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" +
                "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

        // Split up the array of whole names into an array of first/last names
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                .map(name -> name.split(" "))
                .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

        log.info("Querying for customer records where first_name = 'Josh':");
        List<Customer> query = jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[]{"Josh"},
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        );

        query.forEach(customer -> log.info(customer.toString()));

        assertEquals(2, query.size());
        assertEquals("Bloch", query.get(0).last_name);
        assertEquals("Long", query.get(1).last_name);

    }

    @Test
    @AnyStubId(filename = "selectwithaliasTest")
    public void testSelectwithalias() {

        for (int i=0; i<2; i++) {

            log.info("Creating tables");

            jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
            jdbcTemplate.execute("CREATE TABLE customers(" +
                    "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

            // Split up the array of whole names into an array of first/last names
            List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
                    .map(name -> name.split(" "))
                    .collect(Collectors.toList());

            // Use a Java 8 stream to print out each tuple of the list
            splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

            // Uses JdbcTemplate's batchUpdate operation to bulk load data
            jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

            log.info("Querying for customer records where first_name = 'Josh':");
            List<Customer> query = jdbcTemplate.query(
                    "SELECT id idx, first_name first_nameX, last_name last_nameX FROM customers WHERE first_name = ?", new Object[]{"Josh"},
                    (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
            );

            assertEquals(2, query.size());
            assertEquals("Bloch", query.get(0).last_name);
            assertEquals("Long", query.get(1).last_name);

            query = jdbcTemplate.query(
                    "SELECT id idx, first_name first_nameX, last_name last_nameX FROM customers WHERE first_name = ?", new Object[]{"Josh"},
                    (rs, rowNum) -> new Customer(rs.getLong("idx"), rs.getString("first_nameX"), rs.getString("last_nameX"))
            );

            assertEquals(2, query.size());
            assertEquals("Bloch", query.get(0).last_name);
            assertEquals("Long", query.get(1).last_name);
        }
    }


    @Test
    public void testStoreProcedure() {
        jdbcTemplate.execute("DROP ALIAS SP_HELLO if Exists");
        jdbcTemplate.execute("CREATE ALIAS SP_HELLO AS $$\n" +
                "String spHello(String value) {\n" +
                "    return \"HELLO: \"+value;\n" +
                "}\n" +
                "$$;");

        List<String> query = jdbcTemplate.query("call SP_HELLO('XX');", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString(1);
            }

        });

        assertEquals(1, query.size());
        assertEquals("HELLO: XX", query.get(0));
    }

    @Test
    @AnyStubId(filename = "blobTest", requestMode = RequestMode.rmAll)
    public void testBlob() {
        for (int ii=0; ii<2; ii++) {
            jdbcTemplate.execute("DROP TABLE BLOBREPORT IF EXISTS");

            jdbcTemplate.execute("CREATE TABLE BLOBREPORT(\n" +
                    "ID BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "NAME VARCHAR(255) NOT NULL,\n" +
                    "IMAGE BLOB\n" +
                    ");");

            String sql = "insert into BLOBREPORT (NAME, IMAGE) values (?, ?)";
            KeyHolder holder = new GeneratedKeyHolder();
            int affRows = jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection connection)
                        throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, "TEXT");
                    ByteArrayInputStream inputStream = new ByteArrayInputStream("blob content".getBytes());
                    ps.setBlob(2, inputStream);
                    return ps;
                }
            }, holder);

            assertEquals(1, affRows);

            List<String> query = jdbcTemplate.query("select * from BLOBREPORT where id =?", new Object[]{1},
                    (resultSet, i) -> {
                        String s;
                        Blob image = resultSet.getBlob("IMAGE");
                        try (InputStream binaryStream = image.getBinaryStream();
                             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                            int r;
                            while ((r = binaryStream.read()) != -1) {
                                byteArrayOutputStream.write(r);
                            }
                            s = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);

                        } catch (IOException e) {
                            s = null;
                        }
                        return s;
                    }
            );

            assertEquals(1, query.size());
            assertEquals("blob content", query.get(0));
        }
    }


    @Test
    @AnyStubId(filename = "integerLongTest", requestMode = RequestMode.rmAll)
    public void testIntegerLongTest() {
        jdbcTemplate.execute("DROP TABLE SOMETYPES IF EXISTS");

        String sql = "CREATE TABLE SOMETYPES(\n" +
                "ID BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
                "NAME INT NOT NULL,\n" +
                "C_BIGINT BIGINT, \n" +
                "C_SMALLINT SMALLINT, \n" +
                "C_BOOL BOOL, \n" +
                "C_DECIMAL DECIMAL, \n" +
                "C_DOUBLE DOUBLE, \n" +
                "C_TIME TIME, \n" +
                "C_DATE DATE, \n" +
                "C_TIMESTAMP TIMESTAMP \n" +
                ");";
        jdbcTemplate.execute(sql);

        String sqlI = "insert into SOMETYPES (NAME, " +
                "C_BIGINT," +
                "C_SMALLINT," +
                "C_BOOL," +
                "C_DECIMAL," +
                "C_DOUBLE," +
                "C_TIME," +
                "C_DATE," +
                "C_TIMESTAMP) values (?, ?,?, ?,?, ?,?, ?,?)";
        KeyHolder holder = new GeneratedKeyHolder();
        int affectedRows = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlI);
                preparedStatement.setInt(1, 1);
                preparedStatement.setLong(2, Integer.MAX_VALUE + 1L);
                preparedStatement.setShort(3, (short) 126);
                preparedStatement.setBoolean(4, true);
                preparedStatement.setFloat(5, 14);
                preparedStatement.setDouble(6, 43.124);
                preparedStatement.setTime(7, Time.valueOf("17:23:54"));
                preparedStatement.setDate(8, Date.valueOf("2019-04-22"));
                preparedStatement.setTimestamp(9, new Timestamp(1235385345));
                return preparedStatement;
            }
        }, holder);

        assertEquals(1, affectedRows);

        List<String> query;

        query = jdbcTemplate.query("select * from SOMETYPES where id =?", new Object[]{1},
                (resultSet, i) -> resultSet.getInt("NAME") + " " +
                        resultSet.getLong("C_BIGINT") + " " +
                        resultSet.getFloat("C_DECIMAL") + " " +
                        resultSet.getBoolean("C_BOOL") + " " +
                        resultSet.getFloat("C_SMALLINT") + " " +
                        resultSet.getDouble("C_DOUBLE") + " " +
                        resultSet.getTime("C_TIME") + " " +
                        resultSet.getDate("C_DATE") + " " +
                        resultSet.getTimestamp("C_TIMESTAMP").getTime()
        );

        assertEquals(1, query.size());
        assertEquals("1 2147483648 14.0 true 126.0 43.124 17:23:54 2019-04-22 1235385345",
                query.get(0));


        query = jdbcTemplate.query("select * from SOMETYPES where id =?", new Object[]{1},
                (resultSet, i) -> resultSet.getInt(2) + " " +
                        resultSet.getLong(3) + " " +
                        resultSet.getShort(4) + " " +
                        resultSet.getBoolean(5) + " " +
                        resultSet.getFloat(6) + " " +
                        resultSet.getDouble(7) + " " +
                        resultSet.getTime(8) + " " +
                        resultSet.getDate(9) + " " +
                        resultSet.getTimestamp(10).getTime()
        );

        assertEquals(1, query.size());
        assertEquals("1 2147483648 126 true 14.0 43.124 17:23:54 2019-04-22 1235385345",
                query.get(0));
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    public void nullColumnValueTest() {
        jdbcTemplate.execute("DROP TABLE NPETABLE IF EXISTS");

        String sql = "CREATE TABLE NPETABLE(\n" +
                "ID BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
                "C_DATE DATE);";
        jdbcTemplate.execute(sql);

        String sqlI = "insert into NPETABLE (C_DATE) values (?)";
        KeyHolder holder = new GeneratedKeyHolder();
        int affectedRows = jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = connection.prepareStatement(sqlI);
                preparedStatement.setNull(1, Types.DATE);
                return preparedStatement;
            }
        }, holder);

        assertEquals(1, affectedRows);

        List<String> query;

        query = jdbcTemplate.query("select * from NPETABLE where id =?", new Object[]{1},
                (resultSet, i) -> " " + resultSet.getDate("C_DATE"));

        assertEquals(1, query.size());
        assertEquals(" null", query.get(0));
    }

    @Test
    @AnyStubId(filename = "integerLongTest1", requestMode = RequestMode.rmAll)
    public void testIntegerLong1() throws SQLException {
        for (int i=0; i<2; i++) {
            jdbcTemplate.execute("DROP ALIAS SP_HELLO2 if Exists");
            jdbcTemplate.execute("CREATE ALIAS SP_HELLO2 AS $$\n" +
                    "String spHello(String value) {\n" +
                    "    return \"HELLO: \"+value;\n" +
                    "}\n" +
                    "$$;");

            List<SqlParameter> l = new ArrayList<>();
            SqlParameter sqlParameter = new SqlParameter("XX", VARCHAR);
            l.add(sqlParameter);

            Map<String, Object> res = jdbcTemplate.call(new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                    CallableStatement callableStatement = connection.prepareCall("call  SP_HELLO2(?);");
                    callableStatement.setString(1, "attempt 1");
                    return callableStatement;
                }
            }, l);
            assertNotNull(res);

            ResultSet rs;
            rs = jdbcTemplate.execute(new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection connection) throws SQLException {
                    CallableStatement callableStatement = connection.prepareCall("call SP_HELLO2(?);");
                    callableStatement.setString(1, "xx");
                    return callableStatement;
                }
            }, new CallableStatementCallback<ResultSet>() {
                @Override
                public ResultSet doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                    callableStatement.execute();
                    return callableStatement.getResultSet();
                }
            });

            assertTrue(rs.next());
            assertEquals("HELLO: xx", rs.getString(1));

            // invent a test with callableStatement.setString("xx", "attempt 2");
            // org.springframework.jdbc.BadSqlGrammarException: CallableStatementCallback; bad SQL grammar []; nested exception is org.h2.jdbc.JdbcSQLSyntaxErrorException: Syntax error in SQL statement "CALL SP_HELLO2(:[*]XX); "; expected "), NOT, EXISTS, INTERSECTS"; SQL statement:
            //call SP_HELLO2(:xx); [42001-199]

//        rs = jdbcTemplate.execute(new CallableStatementCreator() {
//            @Override
//            public CallableStatement createCallableStatement(Connection connection) throws SQLException {
//                CallableStatement callableStatement = connection.prepareCall("call SP_HELLO2(:xx);");
//
//                return callableStatement;
//            }
//        }, new CallableStatementCallback<ResultSet>() {
//            @Override
//            public ResultSet doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
//                callableStatement.setString("xx", "attempt 2");
//                callableStatement.execute();
//                return callableStatement.getResultSet();
//            }
//        });

//        assertTrue(rs.next());
//        assertEquals("HELLO: attempt 2", rs.getString(1));
        }

    }

    private static List<Connection> connections = Collections.synchronizedList(new ArrayList<>());
    private static List<Statement> statements = Collections.synchronizedList(new ArrayList<>());
    private static List<DatabaseMetaData> metaData = Collections.synchronizedList(new ArrayList<>());

    public void after() {
        Mockito.mockingDetails(dataSource)
                .getInvocations()
                .forEach(x -> {
                    log.info("" + x.getSequenceNumber());
                    log.info(x.getLocation().toString());
                });
        log.info("connections ============== ");
        connections.forEach(c -> {
            log.info(" c ------------------------");
            Mockito.mockingDetails(c)
                    .getInvocations()
                    .forEach(x -> {
                        log.info("" + x.getSequenceNumber());
                        log.info(x.getLocation().toString());
                    });
        });

        log.info("statements =================");
        statements.forEach(s -> {
            log.info(" s ------------------------");
            Mockito.mockingDetails(s)
                    .getInvocations()
                    .forEach(x -> {
                        log.info("" + x.getSequenceNumber());
                        log.info(x.getLocation().toString());
                    });
        });

        log.info("metadata ===================");
        metaData.forEach(m -> {
            log.info("m ----------------------");
            Mockito.mockingDetails(m)
                    .getInvocations()
                    .forEach(x -> {
                        log.info("" + x.getSequenceNumber());
                        log.info(x.getLocation().toString());
                    });
        });

    }





    @Test
    public void testSirs() throws SQLException {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn("ID", Types.INTEGER, 10, 0);
        rs.addColumn("NAME", Types.VARCHAR, 255, 0);
        rs.addRow("2", "Hello");
        rs.addRow(1, "World");

        rs.next();
        assertEquals(2, rs.getLong(1));
        assertEquals(2, rs.getLong("ID"));
        rs.next();
        assertEquals(1, rs.getLong("ID"));

    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    void testUUIDColumn() {
        jdbcTemplate.execute("DROP TABLE UUIDTABLE IF EXISTS");
        String sql = "create table UUIDTABLE(id int primary key, data uuid default 'dbfe22f6-3d1c-4a35-9b52-2ee8ce3c424f')";
        jdbcTemplate.execute(sql);

        jdbcTemplate.execute("insert into UUIDTABLE(id) values(1);");

        List<String> data = jdbcTemplate.query("select * from UUIDTABLE", new Object[]{},
                (resultSet, i) -> String.format("%d %s",
                        resultSet.getInt("ID"),
                        resultSet.getObject("DATA", UUID.class)
                        ));

        assertEquals(1, data.size());
        assertEquals("1 \"dbfe22f6-3d1c-4a35-9b52-2ee8ce3c424f\"", data.get(0));

    }

}
