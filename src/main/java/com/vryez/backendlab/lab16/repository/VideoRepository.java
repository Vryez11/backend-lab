package com.vryez.backendlab.lab16.repository;

import com.vryez.backendlab.lab16.domain.Video;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Repository("lab16VideoRepository")
public class VideoRepository {

    private final DataSource dataSource;

    public VideoRepository(@Qualifier("lab16DataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Video findById(Long id) {
        String sql = "select id, title, view_count, reported from video where id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Video(rs.getLong("id"), rs.getString("title"),
                        rs.getLong("view_count"), rs.getBoolean("reported"), null);
            }
            throw new NoSuchElementException("video not found: id=" + id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }
    }

    public void increaseViewCount(Long id) {
        String sql = "update video set view_count = view_count + 1 where id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }
    }

    public long countAll() {
        String sql = "select count(*) from video";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }
    }

    public String findReportReason(Long videoId) {
        String sql = "select reason from video_report where video_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = dataSource.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, videoId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("reason");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            JdbcUtils.closeConnection(con);
        }
    }
}
