package com.vryez.backendlab.lab16.controller;

import com.vryez.backendlab.lab16.domain.Video;
import com.vryez.backendlab.lab16.repository.VideoRepository;
import com.vryez.backendlab.lab16.service.VideoViewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("lab16VideoController")
@RequestMapping("/lab16/videos")
public class VideoController {

    private final VideoViewService videoViewService;
    private final VideoRepository videoRepository;

    public VideoController(VideoViewService videoViewService, VideoRepository videoRepository) {
        this.videoViewService = videoViewService;
        this.videoRepository = videoRepository;
    }

    @GetMapping("/{id}")
    public Video detail(@PathVariable Long id) {
        return videoViewService.detail(id);
    }

    @GetMapping("/count")
    public long count() {
        return videoRepository.countAll();
    }
}
