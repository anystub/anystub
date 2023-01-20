package org.anystub.it_hikari;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.anystub.Base;
import org.anystub.http.StubHttpClient;
import org.anystub.jdbc.StubDataSource;
import org.anystub.mgmt.BaseManagerFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 *
 */
@Configuration
public class DefaultConfiguration {

    @Bean
    DataSource dataSource() {

        Base base = BaseManagerFactory
                .getBaseManager()
                .getBase("jdbcStub-hk.yml");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:./test4;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE");
        HikariDataSource ds = new HikariDataSource(config);

        DataSource stubDataSource = new StubDataSource(ds)
                .setFallbackBase(base)
                .setStubResultSetMode(true);
        return stubDataSource;
    }
}

