package com.vryez.backendlab.lab16;

import com.vryez.backendlab.lab16.config.VideoLabDataSourceConfig;
import com.vryez.backendlab.lab16.domain.Video;
import com.vryez.backendlab.lab16.repository.VideoRepository;
import com.vryez.backendlab.lab16.service.VideoViewService;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class Lab16GradingTest {

    private HikariDataSource dataSource;
    private VideoRepository repository;
    private VideoViewService service;

    private void initDataSource(String dbName) {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setMaximumPoolSize(VideoLabDataSourceConfig.POOL_SIZE);
        dataSource.setConnectionTimeout(VideoLabDataSourceConfig.CONN_TIMEOUT_MS);
        dataSource.setPoolName(dbName + "-pool");
        DatabasePopulatorUtils.execute(new ResourceDatabasePopulator(
                new ClassPathResource("lab16/schema.sql"),
                new ClassPathResource("lab16/data.sql")), dataSource);
        repository = new VideoRepository(dataSource);
        service = new VideoViewService(repository);
    }

    @AfterEach
    void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    void AT1_신고영상_상세를_풀크기보다_많이_조회해도_후속_조회가_즉시_성공한다() {
        initDataSource("lab16_grading_at1");

        for (int i = 0; i < 10; i++) {
            service.detail(10L);
        }

        long start = System.nanoTime();
        long count = repository.countAll();
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertThat(count).isEqualTo(6);
        assertThat(elapsedMs)
                .as("누수가 없다면 커넥션 대기 없이 즉시 성공해야 한다 (실제: %dms)", elapsedMs)
                .isLessThan(200);
    }

    @Test
    void AT2_일반영상은_아무리_많이_조회해도_풀이_고갈되지_않는다() {
        initDataSource("lab16_grading_at2");

        for (int i = 0; i < 30; i++) {
            service.detail(1L);
        }

        long start = System.nanoTime();
        long count = repository.countAll();
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertThat(count).isEqualTo(6);
        assertThat(elapsedMs).isLessThan(200);
        assertThat(dataSource.getHikariPoolMXBean().getActiveConnections())
                .as("조회가 끝난 뒤 점유 중인 커넥션이 없어야 한다")
                .isZero();
    }

    @Test
    void AT3_풀고갈은_타임아웃만큼_대기한_뒤_획득_타임아웃_시그니처로_실패한다() throws Exception {
        initDataSource("lab16_grading_at3");

        List<Connection> held = new ArrayList<>();
        try {
            for (int i = 0; i < VideoLabDataSourceConfig.POOL_SIZE; i++) {
                held.add(dataSource.getConnection());
            }

            long start = System.nanoTime();
            Throwable thrown = catchThrowable(() -> repository.countAll());
            long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

            assertThat(thrown).isNotNull();
            assertThat(elapsedMs)
                    .as("풀 고갈은 즉시가 아니라 connectionTimeout(%dms) 대기 후 실패해야 한다 (실제: %dms)",
                            VideoLabDataSourceConfig.CONN_TIMEOUT_MS, elapsedMs)
                    .isGreaterThanOrEqualTo(VideoLabDataSourceConfig.CONN_TIMEOUT_MS);

            Throwable terminal = thrown;
            while (terminal.getCause() != null) {
                terminal = terminal.getCause();
            }
            assertThat(terminal.getMessage())
                    .as("원인 체인 말단이 커넥션 획득 타임아웃을 가리켜야 한다")
                    .contains("Connection is not available");
        } finally {
            for (Connection c : held) {
                c.close();
            }
        }
    }

    @Test
    void AT4_기존_조회는_수정_후에도_정상값을_반환한다() {
        initDataSource("lab16_grading_at4");

        Video video = repository.findById(1L);
        assertThat(video.getTitle()).isEqualTo("스프링 입문");
        assertThat(video.getViewCount()).isZero();
        assertThat(video.isReported()).isFalse();

        repository.increaseViewCount(1L);
        assertThat(repository.findById(1L).getViewCount()).isEqualTo(1);

        assertThat(repository.countAll()).isEqualTo(6);
    }
}
