package com.vryez.backendlab.lab07;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository("lab07VideoRepository")
public class VideoRepository {

    private final Map<Long, Video> store = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        store.put(1L, new Video(1L, "토스 송금 튜토리얼", "viewer", 1200));
        store.put(2L, new Video(2L, "카드 발급 안내", "viewer", 850));
        store.put(3L, new Video(3L, "관리자 공지", "admin", 4300));
    }

    public List<Video> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Video::getId))
                .toList();
    }

    public Optional<Video> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
