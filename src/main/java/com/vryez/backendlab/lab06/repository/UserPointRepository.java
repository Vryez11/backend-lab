package com.vryez.backendlab.lab06.repository;

import com.vryez.backendlab.lab06.domain.UserPoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserPointRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserPointRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<UserPoint> findById(String userId) {
        String sql = "select user_id, point, gift_enabled from user_point where user_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(new UserPoint(
                        rs.getString("user_id"),
                        rs.getLong("point"),
                        rs.getBoolean("gift_enabled")));
            }
            return Optional.empty();
        }, userId);
    }

    public void updatePoint(String userId, long point) {
        jdbcTemplate.update("update user_point set point = ? where user_id = ?", point, userId);
    }

    public void save(UserPoint userPoint) {
        jdbcTemplate.update(
                "merge into user_point (user_id, point, gift_enabled) key (user_id) values (?, ?, ?)",
                userPoint.getUserId(), userPoint.getPoint(), userPoint.isGiftEnabled());
    }
}
