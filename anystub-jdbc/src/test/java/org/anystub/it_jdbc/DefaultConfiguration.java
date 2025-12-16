package org.anystub.it_jdbc;

import org.anystub.jdbc.StubDataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DefaultConfiguration {

    @Bean
    DataSource dataSource() {

        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:./test5;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE");

        return new StubDataSource(ds);
    }

}
