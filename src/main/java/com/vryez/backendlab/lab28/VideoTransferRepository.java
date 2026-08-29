package com.vryez.backendlab.lab28;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VideoTransferRepository {

    private final JdbcTemplate jdbcTemplate;

    public String findStatus(long videoId) {
        return jdbcTemplate.queryForObject(
                "select status from video where id = ?", String.class, videoId);
    }

    public void updateChannelId(long videoId, long channelId) {
        jdbcTemplate.update(
                "update video set channel_id = ? where id = ?", channelId, videoId);
    }

    public void addVideoCount(long channelId, int delta) {
        jdbcTemplate.update(
                "update channel set video_count = video_count + ? where id = ?", delta, channelId);
    }

    public void insertTransferLog(long videoId, long fromChannelId, long toChannelId) {
        jdbcTemplate.update(
                "insert into transfer_log(video_id, from_channel_id, to_channel_id, moved_at) values (?, ?, ?, current_timestamp)",
                videoId, fromChannelId, toChannelId);
    }

    public long findChannelId(long videoId) {
        return jdbcTemplate.queryForObject(
                "select channel_id from video where id = ?", Long.class, videoId);
    }

    public int findVideoCount(long channelId) {
        return jdbcTemplate.queryForObject(
                "select video_count from channel where id = ?", Integer.class, channelId);
    }

    public int countTransferLogs() {
        return jdbcTemplate.queryForObject(
                "select count(*) from transfer_log", Integer.class);
    }
}
