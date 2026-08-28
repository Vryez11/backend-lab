package com.vryez.backendlab.lab27;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("lab27AuditLogRepository")
@RequiredArgsConstructor
public class AuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public void save(long videoId, String uploaderId, String videoTitle) {
        jdbcTemplate.update(
                "insert into lab27_upload_audit_log(video_id, uploader_id, video_title, created_at)"
                        + " values (?, ?, ?, current_timestamp)",
                videoId, uploaderId, videoTitle);
    }
}
