package com.vryez.backendlab.lab13;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Lab13Config {

    @Bean
    public ApplicationRunner giftSchemaInit(JdbcTemplate jdbcTemplate) {
        return args -> jdbcTemplate.execute(
                "create table if not exists gift (" +
                        "id bigint auto_increment primary key, " +
                        "viewer_name varchar(50) not null, " +
                        "amount bigint not null, " +
                        "created_at timestamp default current_timestamp)");
    }
}
