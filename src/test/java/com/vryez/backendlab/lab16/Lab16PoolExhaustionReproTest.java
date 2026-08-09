package com.vryez.backendlab.lab16;

import com.vryez.backendlab.lab16.config.VideoLabDataSourceConfig;
import com.vryez.backendlab.lab16.repository.VideoRepository;
import com.vryez.backendlab.lab16.service.VideoViewService;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.SQLTransientConnectionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class Lab16PoolExhaustionReproTest {

    private static final long REPORTED_VIDEO_ID = 10L;

    private static HikariDataSource dataSource;
    private static VideoRepository repository;
    private static VideoViewService service;

    @BeforeAll
    static void setUp() {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:videolab16_repro;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setMaximumPoolSize(VideoLabDataSourceConfig.POOL_SIZE);
        dataSource.setConnectionTimeout(VideoLabDataSourceConfig.CONN_TIMEOUT_MS);
        dataSource.setPoolName("VideoLabReproPool");
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
    void 신고영상_상세를_풀크기만큼_조회하면_다음_조회는_커넥션_획득_타임아웃으로_실패한다() {
        for (int i = 0; i < VideoLabDataSourceConfig.POOL_SIZE; i++) {
            service.detail(REPORTED_VIDEO_ID);
        }

        long startNanos = System.nanoTime();
        Throwable thrown = catchThrowable(() -> repository.countAll());
        long waitedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        assertThat(thrown)
                .as("풀이 고갈됐다면 후속 조회는 예외로 실패해야 한다")
                .isNotNull();

        assertThat(waitedMs)
                .as("풀 고갈이라면 connectionTimeout(%dms) 이상 대기한 뒤 실패해야 한다 (실제 대기: %dms)",
                        VideoLabDataSourceConfig.CONN_TIMEOUT_MS, waitedMs)
                .isGreaterThanOrEqualTo(VideoLabDataSourceConfig.CONN_TIMEOUT_MS);

        Throwable acquisitionTimeout = findInCauseChain(thrown, SQLTransientConnectionException.class);
        assertThat(acquisitionTimeout)
                .as("원인 체인에 커넥션 획득 타임아웃이 있어야 한다. 실제 예외: %s", String.valueOf(thrown))
                .isNotNull();
        assertThat(acquisitionTimeout.getMessage())
                .as("예외 메시지가 커넥션 획득 타임아웃임을 가리켜야 한다")
                .contains("Connection is not available")
                .contains("request timed out");
    }

    private static Throwable findInCauseChain(Throwable root, Class<? extends Throwable> type) {
        for (Throwable t = root; t != null; t = t.getCause()) {
            if (type.isInstance(t)) {
                return t;
            }
        }
        return null;
    }
}
