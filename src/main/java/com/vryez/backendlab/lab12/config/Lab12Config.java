package com.vryez.backendlab.lab12.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class Lab12Config {

    @Bean
    public ApplicationRunner payoutSchemaInit(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute(
                    "create table if not exists creator (" +
                            "id bigint auto_increment primary key, " +
                            "name varchar(50) not null, " +
                            "payout_balance bigint not null)");
            jdbcTemplate.execute(
                    "create table if not exists payout (" +
                            "id bigint auto_increment primary key, " +
                            "creator_id bigint not null, " +
                            "amount bigint not null, " +
                            "status varchar(20) not null, " +
                            "created_at timestamp not null default current_timestamp)");
            jdbcTemplate.update(
                    "merge into creator (id, name, payout_balance) key (id) values (1, '우지', 1000000)");
        };
    }
}
