package com.vryez.backendlab.lab14;

import java.time.LocalDateTime;

public record VideoResponse(Long id, String title, long views, LocalDateTime createdAt) {

    public static VideoResponse from(Video video) {
        return new VideoResponse(video.id(), video.title(), video.views(), video.createdAt());
    }
}
