package com.vryez.backendlab.lab08;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Lab08AcceptanceTest {

    private final VideoRecommendationService service =
            new VideoRecommendationService(new InMemoryVideoRepository(), new ViewCountRankingPolicy());

    @Test
    @DisplayName("컨테이너 없이: 같은 업로더의 다른 영상을 조회수 내림차순 최대 N개 추천")
    void 추천_정확성() {
        List<Video> result = service.recommendFrom(1L, 3);
        assertThat(result).extracting(Video::getId).containsExactly(2L, 4L, 3L);
    }

    @Test
    @DisplayName("limit 경계: 2개 요청 시 상위 2개, 초과 요청 시 있는 만큼만")
    void limit_경계() {
        assertThat(service.recommendFrom(1L, 2)).extracting(Video::getId).containsExactly(2L, 4L);
        assertThat(service.recommendFrom(1L, 10)).extracting(Video::getId).containsExactly(2L, 4L, 3L);
    }

    @Test
    @DisplayName("제외 규칙: 기준 영상(1)과 다른 업로더 영상(5)은 절대 포함되지 않는다")
    void 제외_규칙() {
        assertThat(service.recommendFrom(1L, 10))
                .extracting(Video::getId)
                .doesNotContain(1L, 5L);
    }

    @Test
    @DisplayName("없는 영상이면 IllegalArgumentException")
    void 없는_영상() {
        assertThatThrownBy(() -> service.recommendFrom(999L, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("구조 검증: 모든 협력 필드 final, 필드 주입·setter 없음, 기본 생성자 없음")
    void 구조_검증() throws Exception {
        Class<VideoRecommendationService> clazz = VideoRecommendationService.class;

        for (Field field : clazz.getDeclaredFields()) {
            assertThat(Modifier.isFinal(field.getModifiers()))
                    .as("협력 필드 %s는 final이어야 한다", field.getName())
                    .isTrue();
            assertThat(field.isAnnotationPresent(Autowired.class))
                    .as("협력 필드 %s에 필드 주입(@Autowired)이 없어야 한다", field.getName())
                    .isFalse();
        }

        for (Method method : clazz.getDeclaredMethods()) {
            assertThat(method.getName())
                    .as("협력 객체를 교체하는 setter가 없어야 한다")
                    .doesNotStartWith("set");
        }

        assertThatThrownBy(clazz::getDeclaredConstructor)
                .as("협력 객체 없이 생성하는 경로(기본 생성자)가 존재하면 안 된다")
                .isInstanceOf(NoSuchMethodException.class);
    }
}
