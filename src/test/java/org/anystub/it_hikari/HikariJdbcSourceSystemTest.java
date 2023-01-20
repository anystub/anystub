package org.anystub.it_hikari;

import org.anystub.AnyStubId;
import org.anystub.RequestMode;
import org.anystub.mgmt.BaseManagerFactory;
import org.anystub.src.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@SpringBootTest
@AnyStubId
public class HikariJdbcSourceSystemTest {
    private final static Logger log = Logger.getLogger("test");
    
    @Autowired
    JdbcTemplate jdbcTemplate;


    @Autowired
    DataSource dataSource;

    @Test()
    public void testSome() {

        log.info("Creating tables");
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);

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
    @AnyStubId(filename = "testcasefile", requestMode = RequestMode.rmNone)
    public void testCaseFileTest() {
        jdbcTemplate.execute("DROP TABLE testcasefile IF EXISTS");
        assertTrue("no exceptions expected", true);
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmPassThrough)
    public void testPassThrough() {
        jdbcTemplate.execute("DROP TABLE testcasefileX IF EXISTS");
        assertTrue("no exceptions expected", true);
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmTrack, filename = "testCaseNameTest")
    public void testCaseName() {

        jdbcTemplate.execute("DROP TABLE testcasename IF EXISTS");
        jdbcTemplate.execute("DROP TABLE testcasename IF EXISTS");
        jdbcTemplate.execute("DROP TABLE testcasename IF EXISTS");

        assertEquals(3, BaseManagerFactory.locate().times());
        assertTrue("no exceptions expected", true);
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmAll)
    public void testCallAStatement() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from dual");

        assertEquals(true, resultSet.next());
        assertEquals(false, resultSet.next());
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmNone)
    public void testCallAStatementNone() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from dual");

        assertEquals(true, resultSet.next());
        assertEquals(false, resultSet.next());
    }

    @Test
    @AnyStubId(requestMode = RequestMode.rmNew, filename = "callAStatementFromStub")
    public void testCallAStatementFromStub() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from dual");

        assertEquals(true, resultSet.next());


    }

}
