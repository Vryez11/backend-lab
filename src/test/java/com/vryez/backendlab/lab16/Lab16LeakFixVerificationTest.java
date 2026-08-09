package com.vryez.backendlab.lab16;

import com.vryez.backendlab.lab16.config.VideoLabDataSourceConfig;
import com.vryez.backendlab.lab16.domain.Video;
import com.vryez.backendlab.lab16.repository.VideoRepository;
import com.vryez.backendlab.lab16.service.VideoViewService;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class Lab16LeakFixVerificationTest {

    private static final long REPORTED_VIDEO_ID = 10L;
    private static final int DETAIL_CALLS = 10; // 풀 최대 크기(3)보다 충분히 많이

    private static HikariDataSource dataSource;
    private static VideoRepository repository;
    private static VideoViewService service;

    @BeforeAll
    static void setUp() {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:videolab16_fixcheck;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setMaximumPoolSize(VideoLabDataSourceConfig.POOL_SIZE);
        dataSource.setConnectionTimeout(VideoLabDataSourceConfig.CONN_TIMEOUT_MS);
        dataSource.setPoolName("VideoLabFixCheckPool");
        DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(
                new ClassPathResource("lab16/schema.sql"),
                new ClassPathResource("lab16/data.sql")), dataSource);
        repository = new VideoRepository(dataSource);
        service = new VideoViewService(repository);
    }

    @AfterAll
    static void tearDown() {
        dataSource.close();
    }

    @Test
    void 신고영상_상세를_풀크기보다_많이_조회해도_다음_조회가_즉시_성공한다() {
        long baseViewCount = repository.findById(REPORTED_VIDEO_ID).getViewCount();

        for (int i = 0; i < DETAIL_CALLS; i++) {
            Video video = service.detail(REPORTED_VIDEO_ID);
            assertThat(video.getModerationReason())
                    .as("신고 영상 상세는 수정 후에도 신고 사유를 정상 반환해야 한다 (%d번째 호출)", i + 1)
                    .isEqualTo("선정성 신고");
        }

        long startNanos = System.nanoTime();
        long total = repository.countAll();
        long waitedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        assertThat(total)
                .as("countAll은 기존과 같은 정상값을 반환해야 한다")
                .isEqualTo(6);
        assertThat(waitedMs)
                .as("풀에 여유가 있다면 connectionTimeout(%dms) 대기 없이 즉시 성공해야 한다 (실제: %dms)",
                        VideoLabDataSourceConfig.CONN_TIMEOUT_MS, waitedMs)
                .isLessThan(VideoLabDataSourceConfig.CONN_TIMEOUT_MS);

        assertThat(dataSource.getHikariPoolMXBean().getActiveConnections())
                .as("모든 조회가 끝난 시점에 풀에 반납되지 않은 커넥션이 없어야 한다")
                .isZero();

        assertThat(repository.findById(REPORTED_VIDEO_ID).getViewCount())
                .as("조회수는 상세 조회 횟수만큼 증가해야 한다")
                .isEqualTo(baseViewCount + DETAIL_CALLS);
    }
}
