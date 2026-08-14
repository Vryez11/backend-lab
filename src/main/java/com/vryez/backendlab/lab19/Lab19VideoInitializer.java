package com.vryez.backendlab.lab19;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class Lab19VideoInitializer {

    private final JdbcTemplate jdbc;

    public Lab19VideoInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS lab19_video (
                    id BIGINT PRIMARY KEY,
                    title VARCHAR(200),
                    thumbnail_key VARCHAR(100),
                    view_count BIGINT
                )
                """);
        jdbc.update("DELETE FROM lab19_video");
        jdbc.update("INSERT INTO lab19_video VALUES (?, ?, ?, ?)", 1001, "클로드 코드 입문", "v1001", 52000);
        jdbc.update("INSERT INTO lab19_video VALUES (?, ?, ?, ?)", 1002, "스프링 빈 생명주기", "v1002", 13400);
        jdbc.update("INSERT INTO lab19_video VALUES (?, ?, ?, ?)", 1003, "H2로 배우는 JDBC", "v1003", 8700);
    }
}
