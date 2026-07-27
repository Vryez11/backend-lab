package com.vryez.backendlab.lab05.videoview;

import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

public class VideoViewRepository {

    private final DataSource dataSource;

    public VideoViewRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long getViewCount(String videoId) {
        String sql = "select view_count from video_view where video_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, videoId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("view_count");
            }
            throw new NoSuchElementException("video not found: videoId=" + videoId);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            close(con, pstmt, rs);
        }
    }

    public void increaseViewCount(String videoId) {
        String sql = "update video_view set view_count = view_count + 1 where video_id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, videoId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
}
