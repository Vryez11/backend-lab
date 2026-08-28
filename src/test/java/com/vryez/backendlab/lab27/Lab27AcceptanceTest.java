package com.vryez.backendlab.lab27;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 주의: 테스트에 @Transactional을 붙이지 않는다 — 실제 커밋/롤백 결과를 DB 재조회로 검증한다.
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class Lab27AcceptanceTest {

    static final String SHORT_TITLE = "짧은제목";
    static final String LONG_TITLE = "이 제목은 감사 로그 컬럼 15자를 훨씬 넘는 긴 제목이다"; // 30자

    @Autowired
    VideoUploadService videoUploadService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("delete from lab27_upload_audit_log");
        jdbcTemplate.update("delete from lab27_video_stat");
        jdbcTemplate.update("delete from lab27_video");
    }

    @Test
    @DisplayName("T1 정상: 감사 적재가 성공하는 짧은 제목 → 영상+통계+감사 로그 모두 저장")
    void t1_감사_성공시_셋_다_저장() {
        long videoId = videoUploadService.upload(request(SHORT_TITLE, "user-1"));

        assertThat(videoCountOf(videoId)).isEqualTo(1);
        assertThat(statCountOf(videoId)).isEqualTo(1);
        // 감사 적재는 커밋 이후 비동기 — 잠시 대기 후 단언한다.
        awaitUntil(() -> auditCount() == 1);
        assertThat(auditCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("T2 감사 실패 격리: 긴 제목 → 예외 없이 성공, 영상은 원문 제목 그대로 + 통계 저장, 감사만 0건, 건너뛴 사실이 로그에 남는다")
    void t2_감사_실패는_업로드를_오염시키지_않는다(CapturedOutput output) {
        long videoId = videoUploadService.upload(request(LONG_TITLE, "user-1"));

        String savedTitle = jdbcTemplate.queryForObject(
                "select title from lab27_video where id = ?", String.class, videoId);
        assertThat(savedTitle).isEqualTo(LONG_TITLE);
        assertThat(statCountOf(videoId)).isEqualTo(1);
        // 비동기 감사가 "시도 후 실패"까지 간 것을 로그로 확인한 뒤에 0건을 단언한다
        // (리스너가 아직 실행 전이라 우연히 0건인 것과 구별하기 위함).
        awaitUntil(() -> (output.getOut() + output.getErr()).contains("감사 적재 건너뜀"));
        assertThat(output.getOut() + output.getErr()).contains("감사 적재 건너뜀");
        assertThat(auditCount()).isZero();
    }

    @Test
    @DisplayName("T3 핵심 원자성: 감사 실패 상황에서도 영상과 통계는 항상 쌍으로 존재")
    void t3_영상과_통계는_쌍으로_저장된다() {
        long videoId = videoUploadService.upload(request(LONG_TITLE, "user-2"));

        Integer videoTotal = jdbcTemplate.queryForObject(
                "select count(*) from lab27_video", Integer.class);
        Integer statTotal = jdbcTemplate.queryForObject(
                "select count(*) from lab27_video_stat", Integer.class);
        assertThat(videoTotal).isEqualTo(1);
        assertThat(statTotal).isEqualTo(1);
        assertThat(statCountOf(videoId)).isEqualTo(1);
    }

    @Test
    @DisplayName("T4 회귀: 영상 저장 자체가 실패(uploaderId null)하면 영상·통계·감사 모두 저장되지 않는다")
    void t4_핵심_실패시_전체_롤백() {
        assertThatThrownBy(() -> videoUploadService.upload(request(SHORT_TITLE, null)))
                .isInstanceOf(DataIntegrityViolationException.class);

        Integer videoTotal = jdbcTemplate.queryForObject(
                "select count(*) from lab27_video", Integer.class);
        Integer statTotal = jdbcTemplate.queryForObject(
                "select count(*) from lab27_video_stat", Integer.class);
        assertThat(videoTotal).isZero();
        assertThat(statTotal).isZero();
        assertThat(auditCount()).isZero();
    }

    private void awaitUntil(java.util.function.BooleanSupplier condition) {
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline && !condition.getAsBoolean()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private VideoUploadRequest request(String title, String uploaderId) {
        VideoUploadRequest req = new VideoUploadRequest();
        req.setTitle(title);
        req.setUploaderId(uploaderId);
        return req;
    }

    private Integer videoCountOf(long videoId) {
        return jdbcTemplate.queryForObject(
                "select count(*) from lab27_video where id = ?", Integer.class, videoId);
    }

    private Integer statCountOf(long videoId) {
        return jdbcTemplate.queryForObject(
                "select count(*) from lab27_video_stat where video_id = ?", Integer.class, videoId);
    }

    private Integer auditCount() {
        return jdbcTemplate.queryForObject(
                "select count(*) from lab27_upload_audit_log", Integer.class);
    }
}
