package com.vryez.backendlab.lab12.repository;

import com.vryez.backendlab.lab12.domain.Creator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CreatorRepository {

    private final JdbcTemplate jdbcTemplate;

    public CreatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Creator findById(Long id) {
        return jdbcTemplate.queryForObject(
                "select id, name, payout_balance from creator where id = ?",
                (rs, rowNum) -> new Creator(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getLong("payout_balance")),
                id);
    }

    public void decreaseBalance(Long id, long amount) {
        jdbcTemplate.update(
                "update creator set payout_balance = payout_balance - ? where id = ?",
                amount, id);
    }
}
