package com.vryez.backendlab.lab21;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository("lab21VideoRepository")
public class VideoRepository {

    private final JdbcTemplate jdbcTemplate;

    public VideoRepository(@Qualifier("lab21DataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public long save(VideoUploadRequest req) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "insert into video(handle, title, category, duration_sec) values (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, req.getHandle());
            ps.setString(2, req.getTitle());
            ps.setString(3, req.getCategory());
            ps.setInt(4, req.getDurationSec());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from video", Integer.class);
    }
}
