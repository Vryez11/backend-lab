package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository("lab27VideoRepository")
@RequiredArgsConstructor
public class VideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public long save(String title, String uploaderId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into lab27_video(title, uploader_id, created_at) values (?, ?, current_timestamp)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, uploaderId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }
}
