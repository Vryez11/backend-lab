package com.vryez.backendlab.lab08;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryVideoRepository implements VideoRepository {

    private final Map<Long, Video> store = Map.of(
            1L, new Video(1L, "스프링 입문 1강", 100L, 500),
            2L, new Video(2L, "스프링 입문 2강", 100L, 1200),
            3L, new Video(3L, "스프링 입문 3강", 100L, 300),
            4L, new Video(4L, "스프링 입문 4강", 100L, 900),
            5L, new Video(5L, "완전 다른 채널 영상", 200L, 9999)
    );

    @Override
    public Optional<Video> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Video> findByUploaderExcept(Long uploaderId, Long excludeVideoId) {
        return store.values().stream()
                .filter(v -> v.getUploaderId().equals(uploaderId))
                .filter(v -> !v.getId().equals(excludeVideoId))
                .toList();
    }
}
