package com.vryez.backendlab.lab14;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository("lab14VideoRepository")
public class VideoRepository {

    private final List<Video> videos = new ArrayList<>();

    public VideoRepository() {
        for (long id = 1; id <= 23; id++) {
            long hourOffset = (id * 13) % 23;
            LocalDateTime createdAt = LocalDateTime.of(2026, 7, 1, 0, 0).plusHours(hourOffset);
            long views = ((id * 7) % 23) * 100 + id;
            videos.add(new Video(id, "동영상 " + id, views, createdAt));
        }
    }

    /**
     * 삽입 순서 그대로 반환한다. 정렬은 호출자의 책임이다.
     */
    public List<Video> findAll() {
        return List.copyOf(videos);
    }
}
