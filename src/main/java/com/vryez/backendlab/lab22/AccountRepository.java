package com.vryez.backendlab.lab22;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    public Account findById(long id) {
        return jdbcTemplate.queryForObject(
                "select id, name, balance, suspended from account where id = ?",
                (rs, n) -> new Account(rs.getLong("id"), rs.getString("name"),
                        rs.getInt("balance"), rs.getBoolean("suspended")),
                id);
    }

    public void addBalance(long id, int delta) {
        jdbcTemplate.update("update account set balance = balance + ? where id = ?", delta, id);
    }

    public boolean isSuspended(long id) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                "select suspended from account where id = ?", Boolean.class, id));
    }
}
