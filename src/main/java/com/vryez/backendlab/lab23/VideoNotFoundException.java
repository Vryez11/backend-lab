package com.vryez.backendlab.lab23;

public class VideoNotFoundException extends RuntimeException {

    private final Long videoId;

    public VideoNotFoundException(Long videoId) {
        super("영상을 찾을 수 없습니다: id=" + videoId);
        this.videoId = videoId;
    }

    public Long getVideoId() {
        return videoId;
    }
}
