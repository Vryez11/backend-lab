package com.vryez.backendlab.lab08;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ViewCountRankingPolicy implements RankingPolicy {

    @Override
    public List<Video> rank(List<Video> videos, int limit) {
        return videos.stream()
                .sorted(Comparator.comparingLong(Video::getViewCount).reversed())
                .limit(limit)
                .toList();
    }
}
