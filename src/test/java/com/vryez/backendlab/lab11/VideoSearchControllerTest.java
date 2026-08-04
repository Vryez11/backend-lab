package com.vryez.backendlab.lab11;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class VideoSearchControllerTest {

    @Autowired
    private VideoSearchController videoSearchController;

    @Autowired
    private VideoSearchRepository repository;

    @Qualifier("lab11DataSource")
    @Autowired
    private DataSource dataSource;

    @Test
    public void 스프링으로_검색하면_NPE_발생() {

        Assertions.assertThrows(NullPointerException.class, () -> videoSearchController.search("스프링"));
    }

    @Test
    public void 스프링으로_검색을_두번하면_커넥션풀의_커넥션개수는_0개() throws SQLException {

        assertThatThrownBy(() -> videoSearchController.search("스프링"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> videoSearchController.search("스프링"))
                .isInstanceOf(NullPointerException.class);

        // 커넥션 풀 없음.
        assertThatThrownBy(() -> videoSearchController.count())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 문제_검색_2회면_풀이_고갈되고_무관한_count까지_1초_블로킹_후_죽는다() {

        for (int i = 0; i < 2; i++) {

            assertThatThrownBy(() -> videoSearchController.search("스프링"))
                    .isInstanceOf(NullPointerException.class);
        }

        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        assertThat(hikariDataSource.getHikariPoolMXBean().getActiveConnections()).isEqualTo(2);

        long start = System.nanoTime();
        Throwable thrown = catchThrowable(() -> repository.count());
        long end = (System.nanoTime() - start);

        assertThat(end).isGreaterThan(900);
        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(SQLTransientConnectionException.class);
    }
}