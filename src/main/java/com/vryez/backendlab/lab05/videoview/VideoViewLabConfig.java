package com.vryez.backendlab.lab05.videoview;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.Statement;

@Configuration
public class VideoViewLabConfig {

    @Bean
    public HikariDataSource videoViewDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:h2:mem:videoview;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        dataSource.setMaximumPoolSize(3);
        dataSource.setConnectionTimeout(1000);
        dataSource.setPoolName("video-view-pool");
        return dataSource;
    }

    @Bean
    public VideoViewRepository videoViewRepository(HikariDataSource videoViewDataSource) {
        return new VideoViewRepository(videoViewDataSource);
    }

    @Bean
    public ApplicationRunner videoViewSchemaInit(HikariDataSource videoViewDataSource) {
        return args -> {
            try (Connection con = videoViewDataSource.getConnection();
                 Statement stmt = con.createStatement()) {
                stmt.execute("create table if not exists video_view (" +
                        "video_id varchar(50) primary key, " +
                        "view_count bigint not null)");
                stmt.execute("merge into video_view (video_id, view_count) key(video_id) values ('v1', 0)");
            }
        };
    }
}
