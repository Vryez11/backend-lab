package com.vryez.backendlab.lab11;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class Lab11GradingTest {

    @Autowired
    private VideoSearchRepository repository;

    @Qualifier("lab11DataSource")
    @Autowired
    private DataSource lab11DataSource;

    @Test
    @DisplayName("정상 검색은 결과를 돌려준다")
    void 정상_검색() {
        List<VideoDto> result = repository.searchByKeyword("HTTP");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("HTTP 기초 - 상태 코드");
        assertThat(result.get(0).duration()).isEqualTo("6:50");
    }

    @Test
    @DisplayName("완료 조건 4 — 문제 검색은 수정 후에도 여전히 에러로 응답한다")
    void 감춤_방지() {
        assertThatThrownBy(() -> repository.searchByKeyword("스프링"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("완료 조건 3 — 문제 검색 5회 반복 후에도 count는 블로킹 없이 6을 반환한다")
    void 회복_검증() {
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> repository.searchByKeyword("스프링"))
                    .isInstanceOf(NullPointerException.class);
        }

        long start = System.nanoTime();
        long count = repository.count();
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        assertThat(count).isEqualTo(6);
        assertThat(elapsedMs).isLessThan(900);
    }

    @Test
    @DisplayName("예외 경로에서도 커넥션이 풀로 반납된다 (누수 없음)")
    void 커넥션_반납_검증() {
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> repository.searchByKeyword("스프링"))
                    .isInstanceOf(NullPointerException.class);
        }

        HikariDataSource pool = (HikariDataSource) lab11DataSource;
        assertThat(pool.getHikariPoolMXBean().getActiveConnections()).isZero();
    }
}
