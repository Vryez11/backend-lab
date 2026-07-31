package com.vryez.backendlab.lab08;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VideoRecommendationServiceTest {

    @Test
    void 같은_업로더_영상을_조회수_내림차순으로_추천한다() {
        VideoRecommendationService service = new VideoRecommendationService(new InMemoryVideoRepository(), new ViewCountRankingPolicy());
        List<Video> result = service.recommendFrom(1L, 3);
        assertThat(result).extracting(Video::getId)
                .containsExactly(2L, 4L, 3L);
    }
}
