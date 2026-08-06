package com.vryez.backendlab.lab13;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GiftRepository {

    private final JdbcTemplate jdbcTemplate;

    public GiftRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String viewerName, long amount) {
        jdbcTemplate.update(
                "insert into gift (viewer_name, amount) values (?, ?)",
                viewerName, amount);
    }

    public long sumAmount() {
        Long sum = jdbcTemplate.queryForObject(
                "select coalesce(sum(amount), 0) from gift", Long.class);
        return sum != null ? sum : 0L;
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from gift");
    }
}
