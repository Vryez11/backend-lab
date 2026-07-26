package com.vryez.backendlab.lab04;

import org.springframework.stereotype.Service;

@Service
public class WatchPointService {

    private static final int SECONDS_PER_POINT = 10;

    public WatchResponse record(String userId, String videoId, long watchedSeconds) {
        verifyPlayback();
        return new WatchResponse(userId, videoId, earnedPoints(watchedSeconds));
    }

    private long earnedPoints(long watchedSeconds) {
        return watchedSeconds / SECONDS_PER_POINT;
    }

    private void verifyPlayback() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
