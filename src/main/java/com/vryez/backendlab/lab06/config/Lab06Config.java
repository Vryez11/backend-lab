package com.vryez.backendlab.lab06.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Lab06Config {

    @Bean
    public ApplicationRunner userPointSchemaInit(JdbcTemplate jdbcTemplate) {
        return args -> jdbcTemplate.execute(
                "create table if not exists user_point (" +
                        "user_id varchar(30) primary key, " +
                        "point bigint not null default 0, " +
                        "gift_enabled boolean not null default true)");
    }
}
