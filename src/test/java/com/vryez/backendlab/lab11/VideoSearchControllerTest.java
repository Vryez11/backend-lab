package com.vryez.backendlab.lab11;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;


import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
}