package com.vryez.backendlab.lab19;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("lab19VideoRepository")
public class VideoRepository {

    private final JdbcTemplate jdbc;

    public VideoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Lab19Video> findAll() {
        return jdbc.query(
                "SELECT id, title, thumbnail_key, view_count FROM lab19_video ORDER BY view_count DESC",
                (rs, n) -> new Lab19Video(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("thumbnail_key"),
                        rs.getLong("view_count")));
    }
}
