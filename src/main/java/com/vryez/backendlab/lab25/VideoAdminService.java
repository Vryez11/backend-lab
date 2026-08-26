package com.vryez.backendlab.lab25;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoAdminService {

    private final JdbcTemplate jdbcTemplate;

    // 신고 누적 영상 일괄 비공개. 운영 규칙: 하나라도 실패하면 전체 취소되어야 한다.
    public void hideReportedVideos(List<Long> videoIds) throws ModerationException {
        for (Long id : videoIds) {
            hideOne(id);
        }
    }

    @Transactional(rollbackFor = {ModerationException.class})
    public void hideOne(Long videoId) throws ModerationException {
        String status = jdbcTemplate.queryForObject(
                "select status from videos where id = ?", String.class, videoId);
        if ("DELETED".equals(status)) {
            throw new ModerationException("이미 삭제된 영상은 비공개 전환할 수 없습니다: " + videoId);
        }
        jdbcTemplate.update("update videos set status = 'PRIVATE' where id = ?", videoId);
        jdbcTemplate.update(
                "insert into moderation_log(video_id, action) values (?, 'HIDE')", videoId);
    }
}
