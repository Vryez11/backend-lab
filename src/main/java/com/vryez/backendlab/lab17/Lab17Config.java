package com.vryez.backendlab.lab17;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Lab17Config {

    @Bean
    public ApplicationRunner lab17SchemaInit(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute(
                    "create table if not exists lab17_member (" +
                            "id bigint primary key, " +
                            "nickname varchar(50) not null)");
            jdbcTemplate.update("merge into lab17_member (id, nickname) values (1, '철수')");
            jdbcTemplate.update("merge into lab17_member (id, nickname) values (2, '영희')");
            jdbcTemplate.update("merge into lab17_member (id, nickname) values (3, '민수')");
        };
    }
}
