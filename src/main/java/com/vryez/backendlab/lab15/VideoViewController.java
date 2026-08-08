package com.vryez.backendlab.lab15;

import org.springframework.web.bind.annotation.*;

/**
 * 급상승 영상 1개만 집계한다고 가정한 단순화 — id 파라미터 없이 단일 카운터.
 */
@RestController
@RequestMapping("/api/videos/trending")
public class VideoViewController {

    private final VideoViewService viewService;

    public VideoViewController(VideoViewService viewService) {
        this.viewService = viewService;
    }

    @PostMapping("/view")   // 조회 1건 발생
    public long view() {
        viewService.view();
        return viewService.getViewCount();
    }

    @GetMapping("/views")   // 현재 집계된 조회수
    public long views() {
        return viewService.getViewCount();
    }
}
