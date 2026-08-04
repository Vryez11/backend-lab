package com.vryez.backendlab.lab11;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VideoSearchRepository {

    private final DataSource dataSource;

    public VideoSearchRepository(@Qualifier("lab11DataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String SEARCH_SQL =
            "SELECT id, title, view_count, duration_sec FROM lab_video WHERE title LIKE ? ORDER BY id";

    private static final String COUNT_SQL =
            "SELECT COUNT(*) FROM lab_video";

    public List<VideoDto> searchByKeyword(String keyword) {
        try(Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(SEARCH_SQL)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            List<VideoDto> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("검색 실패", e);
        }
    }

    public long count() {
        try(Connection con = dataSource.getConnection();
            PreparedStatement ps = con.prepareStatement(COUNT_SQL)) {

            ResultSet rs = ps.executeQuery();
            rs.next();
            long c = rs.getLong(1);
            return c;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private VideoDto map(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String title = rs.getString("title");
        long viewCount = rs.getLong("view_count");
        Integer durationSec = (Integer) rs.getObject("duration_sec");
        String duration = formatDuration(durationSec);
        return new VideoDto(id, title, viewCount, duration);
    }

    private String formatDuration(int totalSeconds) {
        return String.format("%d:%02d", totalSeconds / 60, totalSeconds % 60);
    }
}
