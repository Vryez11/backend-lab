package com.vryez.backendlab.lab08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoRecommendationService {

    private final VideoRepository videoRepository;
    private final RankingPolicy rankingPolicy;

    VideoRecommendationService(VideoRepository videoRepository, RankingPolicy rankingPolicy) {
        this.videoRepository = videoRepository;
        this.rankingPolicy = rankingPolicy;
    }

    public List<Video> recommendFrom(Long videoId, int limit) {
        Video base = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("no video: " + videoId));
        List<Video> sameUploader =
                videoRepository.findByUploaderExcept(base.getUploaderId(), videoId);
        return rankingPolicy.rank(sameUploader, limit);
    }
}
