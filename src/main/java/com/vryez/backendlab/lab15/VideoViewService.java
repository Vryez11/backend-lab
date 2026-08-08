package com.vryez.backendlab.lab15;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class VideoViewService {

    // 급상승 영상 1개의 실시간 조회수 (요청 간 유지되어야 하는 공유 집계값)
    private AtomicLong viewCount = new AtomicLong(0);

    // 조회 1건 반영
    public void view() {
        viewCount.getAndIncrement();
    }

    public long getViewCount() {
        return viewCount.get();
    }

    public void reset() {
        viewCount.set(0);
    }
}
