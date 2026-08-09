package com.vryez.backendlab.lab16.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class VideoLabDataSourceConfig {

    public static final int POOL_SIZE = 3;
    public static final long CONN_TIMEOUT_MS = 1000;

    @Bean("lab16DataSource")
    public DataSource lab16DataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:videolab16;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setMaximumPoolSize(POOL_SIZE);
        ds.setConnectionTimeout(CONN_TIMEOUT_MS);
        ds.setPoolName("VideoLabPool");
        DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(
                new ClassPathResource("lab16/schema.sql"),
                new ClassPathResource("lab16/data.sql")), ds);
        return ds;
    }
}
