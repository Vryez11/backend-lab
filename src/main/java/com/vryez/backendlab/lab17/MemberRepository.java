package com.vryez.backendlab.lab17;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbc;

    public MemberRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<String> findNicknameById(Long id) {
        return jdbc.query("SELECT nickname FROM lab17_member WHERE id = ?",
                (rs, n) -> rs.getString("nickname"), id).stream().findFirst();
    }
}
