package com.vryez.backendlab.lab03;

import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final Map<Long, VideoResponse> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong();

    public VideoController() {
        // 시드 3건 — 제목에 모두 "Spring"이 들어간다
        save("Spring MVC 기본기", "vryez", 600);
        save("Spring DB 트랜잭션", "vryez", 720);
        save("Spring 핵심원리 IoC", "vryez", 540);
    }

    protected VideoResponse save(String title, String uploader, int durationSec) {
        long id = seq.incrementAndGet();
        VideoResponse v = new VideoResponse(id, title, uploader, durationSec);
        store.put(id, v);
        return v;
    }

    // TODO(과제): 요청 파라미터로 keyword(필수)·limit(기본 10)를 받아
    //             store에서 제목에 keyword가 포함(대소문자 무시)된 것을 limit개까지 반환하라.
    //             지금은 빈 배열만 돌려주므로 테스트가 RED다.
    @GetMapping
    public List<VideoResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {

        String needle = keyword.trim().toLowerCase();
        if (needle.isEmpty()) {
            return List.of();
        }

        return store.values().stream()
                .filter(v -> v.title().toLowerCase().contains(needle))
                .limit(limit)
                .toList();
    }

    // TODO(과제): 폼 데이터를 객체로 바인딩해 저장하고, 201 + 생성 리소스(JSON)를 반환하라.
    //             지금은 501을 돌려주므로 테스트가 RED다.
    @PostMapping
    public ResponseEntity<VideoResponse> create(
            @ModelAttribute VideoCreateForm form
    ) {

        VideoResponse video = save(form.getTitle(), form.getUploader(), form.getDurationSec());

        return ResponseEntity
                .created(URI.create("/api/videos/" + video.id()))
                .body(video);
    }
}
