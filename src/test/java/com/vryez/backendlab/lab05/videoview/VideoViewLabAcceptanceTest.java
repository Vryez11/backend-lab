package com.vryez.backendlab.lab05.videoview;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.Statement;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class VideoViewLabAcceptanceTest {

    @Autowired
    VideoViewRepository repository;

    @Autowired
    HikariDataSource videoViewDataSource;

    @BeforeEach
    void resetSeed() throws Exception {
        try (Connection con = videoViewDataSource.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.execute("merge into video_view (video_id, view_count) key(video_id) values ('v1', 0)");
        }
    }

    @Test
    void 제약_풀_설정을_변경하지_않았다() {
        assertThat(videoViewDataSource.getMaximumPoolSize()).isEqualTo(3);
        assertThat(videoViewDataSource.getConnectionTimeout()).isEqualTo(1000L);
    }

    @Test
    void 조회수_10회_연속_증가에도_커넥션_고갈이_발생하지_않는다() {
        for (int i = 0; i < 10; i++) {
            repository.increaseViewCount("v1");
        }
        assertThat(repository.getViewCount("v1")).isEqualTo(10L);
    }

    @Test
    void 조회수가_정확히_누적된다() {
        for (int i = 0; i < 3; i++) {
            repository.increaseViewCount("v1");
        }
        assertThat(repository.getViewCount("v1")).isEqualTo(3L);
    }

    @Test
    void 예외_경로를_20회_반복해도_커넥션이_새지_않는다() {
        for (int i = 0; i < 20; i++) {
            assertThatThrownBy(() -> repository.getViewCount("no-such-id"))
                    .isInstanceOf(NoSuchElementException.class);
        }
        assertThat(repository.getViewCount("v1")).isEqualTo(0L);
        repository.increaseViewCount("v1");
        assertThat(repository.getViewCount("v1")).isEqualTo(1L);
    }
}
