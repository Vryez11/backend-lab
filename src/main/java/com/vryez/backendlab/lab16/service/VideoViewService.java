package com.vryez.backendlab.lab16.service;

import com.vryez.backendlab.lab16.domain.Video;
import com.vryez.backendlab.lab16.repository.VideoRepository;
import org.springframework.stereotype.Service;

@Service("lab16VideoViewService")
public class VideoViewService {

    private final VideoRepository repository;

    public VideoViewService(VideoRepository repository) {
        this.repository = repository;
    }

    public Video detail(Long id) {
        repository.increaseViewCount(id);
        Video video = repository.findById(id);
        if (video.isReported()) {
            video.setModerationReason(repository.findReportReason(id));
        }
        return video;
    }
}
