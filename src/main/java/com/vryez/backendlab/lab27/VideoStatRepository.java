package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("lab27VideoStatRepository")
@RequiredArgsConstructor
public class VideoStatRepository {

    private final JdbcTemplate jdbcTemplate;

    public void init(long videoId) {
        jdbcTemplate.update(
                "insert into lab27_video_stat(video_id, view_count) values (?, 0)", videoId);
    }
}
