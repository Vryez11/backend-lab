package com.vryez.backendlab.lab08;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class Lab08IntegrationTest {

    @Autowired
    VideoRecommendationService service;

    @Test
    @DisplayName("컨테이너가 자동 주입한 서비스도 단위 테스트와 동일하게 동작한다")
    void 통합_동작() {
        List<Video> result = service.recommendFrom(1L, 3);
        assertThat(result).extracting(Video::getId).containsExactly(2L, 4L, 3L);
    }
}
