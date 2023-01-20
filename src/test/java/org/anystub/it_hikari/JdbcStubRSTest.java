package org.anystub.it_hikari;

import org.anystub.AnyStubId;
import org.anystub.Base;
import org.anystub.RequestMode;
import org.anystub.jdbc.StubDataSource;
import org.anystub.mgmt.BaseManagerFactory;
import org.anystub.src.Customer;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest

public class JdbcStubRSTest {

    private Logger log = Logger.getLogger("test");

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @RepeatedTest(2)
    @AnyStubId(requestMode = RequestMode.rmAll, filename = "selectwithaliasTest")
    public void testSelectwithalias() {

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

        Base base = BaseManagerFactory.locate();
        assertTrue(base.timesEx("SELECT.*") >= 2);
    }

}
