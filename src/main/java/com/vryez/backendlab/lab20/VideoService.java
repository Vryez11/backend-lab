package com.vryez.backendlab.lab20;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private final Map<Long, Video> videos = new ConcurrentHashMap<>();

    public VideoService() {
        reset();
    }

    public Video find(Long id) {
        Video video = videos.get(id);
        if (video == null) {
            throw new VideoNotFoundException(id);
        }
        return video;
    }

    public void delete(Long id) {
        if (videos.remove(id) == null) {
            throw new VideoNotFoundException(id);
        }
    }

    /** 테스트용: 시드 데이터로 되돌린다. */
    public void reset() {
        videos.clear();
        videos.put(1L, new Video(1L, "먹방 브이로그 1편"));
        videos.put(2L, new Video(2L, "시골 낚시 라이브 다시보기"));
    }
}
