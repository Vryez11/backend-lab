package com.vryez.backendlab.lab12.repository;

import com.vryez.backendlab.lab12.domain.Payout;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PayoutRepository {

    private final JdbcTemplate jdbcTemplate;

    public PayoutRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Payout payout) {
        jdbcTemplate.update(
                "insert into payout (creator_id, amount, status) values (?, ?, ?)",
                payout.getCreatorId(), payout.getAmount(), payout.getStatus());
    }

    public long sumThisMonthAmount(Long creatorId) {
        Long sum = jdbcTemplate.queryForObject(
                "select coalesce(sum(amount), 0) from payout where creator_id = ? and status = 'PAID'",
                Long.class, creatorId);
        return sum != null ? sum : 0L;
    }

    public int countByCreator(Long creatorId) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from payout where creator_id = ?",
                Integer.class, creatorId);
        return count != null ? count : 0;
    }
}
