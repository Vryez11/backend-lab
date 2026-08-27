package com.vryez.backendlab.lab26;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class VideoUploadService {

    private final JdbcTemplate jdbc;

    private String currentTitle;
    private Long currentUploaderId;

    public VideoUploadService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public UploadResponse upload(UploadRequest req) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            var ps = con.prepareStatement(
                    "INSERT INTO video_lab26(title, uploader_id) VALUES (?, ?)",
                    new String[]{"id"});
            ps.setString(1, req.title());
            ps.setLong(2, req.uploaderId());
            return ps;
        }, keyHolder);
        long videoId = keyHolder.getKey().longValue();

        this.currentTitle = req.title();
        this.currentUploaderId = req.uploaderId();

        // 후처리(썸네일 추출·트랜스코딩 큐 등록) 대기 시뮬레이션
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new UploadResponse(videoId, this.currentTitle, this.currentUploaderId);
    }
}
