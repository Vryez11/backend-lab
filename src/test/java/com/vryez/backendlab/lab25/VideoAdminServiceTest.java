package com.vryez.backendlab.lab25;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 주의: 테스트 자체에 @Transactional을 붙이지 않는다 — 실제 커밋/롤백 여부를 검증해야 한다.
@SpringBootTest
@Sql({"/lab25/schema.sql", "/lab25/data.sql"})
class VideoAdminServiceTest {

    @Autowired
    VideoAdminService videoAdminService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("처리 불가 영상(3번)이 섞이면 예외가 전달되고, DB에는 어떤 변경도 남지 않는다(완전 롤백)")
    void allOrNothing_실패시_완전_롤백() {
        assertThatThrownBy(() -> videoAdminService.hideReportedVideos(List.of(1L, 2L, 3L, 4L, 5L)))
                .isInstanceOf(ModerationException.class);

        assertThat(statusOf(1L)).isEqualTo("PUBLIC");
        assertThat(statusOf(2L)).isEqualTo("PUBLIC");
        assertThat(statusOf(3L)).isEqualTo("DELETED");
        assertThat(statusOf(4L)).isEqualTo("PUBLIC");
        assertThat(statusOf(5L)).isEqualTo("PUBLIC");

        Integer logCount = jdbcTemplate.queryForObject(
                "select count(*) from moderation_log", Integer.class);
        assertThat(logCount).isZero();
    }

    @Test
    @DisplayName("처리 가능한 영상만 있으면 정상 커밋된다 — 전부 PRIVATE, 감사 로그 3건")
    void 전부_성공하면_정상_커밋() throws Exception {
        videoAdminService.hideReportedVideos(List.of(1L, 2L, 4L));

        assertThat(statusOf(1L)).isEqualTo("PRIVATE");
        assertThat(statusOf(2L)).isEqualTo("PRIVATE");
        assertThat(statusOf(4L)).isEqualTo("PRIVATE");

        Integer logCount = jdbcTemplate.queryForObject(
                "select count(*) from moderation_log", Integer.class);
        assertThat(logCount).isEqualTo(3);
    }

    private String statusOf(Long videoId) {
        return jdbcTemplate.queryForObject(
                "select status from videos where id = ?", String.class, videoId);
    }
}
