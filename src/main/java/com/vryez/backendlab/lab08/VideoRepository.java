package com.vryez.backendlab.lab08;

import java.util.List;
import java.util.Optional;

public interface VideoRepository {
    Optional<Video> findById(Long id);

    // 주어진 업로더의 영상 중 excludeVideoId 를 제외하고 전부 반환
    List<Video> findByUploaderExcept(Long uploaderId, Long excludeVideoId);
}
