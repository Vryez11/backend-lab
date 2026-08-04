package com.vryez.backendlab.lab11;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class Lab11DataSourceConfig {

    @Bean("lab11DataSource")
    public DataSource lab11DataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:h2:mem:lab11;DB_CLOSE_DELAY=-1");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setMaximumPoolSize(2);
        ds.setConnectionTimeout(1000);
        ds.setPoolName("lab11-pool");
        DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(
                new ClassPathResource("lab11/schema.sql"),
                new ClassPathResource("lab11/data.sql")), ds);
        return ds;
    }
}
