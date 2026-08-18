package com.vryez.backendlab.lab22;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Lab22Config {

    @Bean
    public ApplicationRunner lab22SchemaInit(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute(
                    "create table if not exists account (" +
                            "id bigint primary key, " +
                            "name varchar(50) not null, " +
                            "balance int not null, " +
                            "suspended boolean not null)");
            jdbcTemplate.update("merge into account (id, name, balance, suspended) values (1, '시청자보내는사람', 1000, false)");
            jdbcTemplate.update("merge into account (id, name, balance, suspended) values (2, '정지크리에이터', 0, true)");
            jdbcTemplate.update("merge into account (id, name, balance, suspended) values (3, '정상크리에이터', 0, false)");
        };
    }
}
