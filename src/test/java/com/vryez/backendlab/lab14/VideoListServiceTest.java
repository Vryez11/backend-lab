package com.vryez.backendlab.lab14;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VideoListServiceTest {

    private VideoRepository repository = new VideoRepository();
    private VideoListService videoListService = new VideoListService(repository);

    @Test
    void cond_아무_파라미터_안왔을때() {

        VideoSearchCond cond = new VideoSearchCond();

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        assertThat(list.content().size()).isEqualTo(10);
        assertThat(list.page()).isEqualTo(cond.getPage());
        assertThat(list.size()).isEqualTo(cond.getSize());
        assertThat(list.totalCount()).isEqualTo(23);
    }

    @Test
    void cond_page_파라미터_만왔을때() {

        VideoSearchCond cond = new VideoSearchCond();
        cond.setPage(2);

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        assertThat(list.content().size()).isEqualTo(3);
        assertThat(list.page()).isEqualTo(cond.getPage());
        assertThat(list.size()).isEqualTo(cond.getSize());
        assertThat(list.totalCount()).isEqualTo(23);
    }

    @Test
    void cond_size_파라미터_만왔을때() {

        VideoSearchCond cond = new VideoSearchCond();
        cond.setSize(5);

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        assertThat(list.content().size()).isEqualTo(5);
        assertThat(list.page()).isEqualTo(cond.getPage());
        assertThat(list.size()).isEqualTo(cond.getSize());
        assertThat(list.totalCount()).isEqualTo(23);
    }

    @Test
    void cond_sort_파라미터_만왔을때() {

        VideoSearchCond cond = new VideoSearchCond();
        cond.setSort("views");

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        assertThat(list.content().size()).isEqualTo(10);
        assertThat(list.page()).isEqualTo(cond.getPage());
        assertThat(list.size()).isEqualTo(cond.getSize());
        assertThat(list.totalCount()).isEqualTo(23);

        long prev = list.content().getFirst().views();
        for (VideoResponse videoResponse : list.content()) {

            assertThat(prev).isGreaterThanOrEqualTo(videoResponse.views());
        }

        // 페이지 내부 순서만이 아니라 전체 기준으로도 맞는지 — 전체 최대 조회수 영상이 맨 앞이어야 한다
        long globalMaxViews = repository.findAll().stream()
                .mapToLong(Video::views)
                .max()
                .orElseThrow();
        assertThat(list.content().getFirst().views()).isEqualTo(globalMaxViews);
    }

    @Test
    void 기본정렬_latest는_전체에서_가장_최신_영상이_맨앞이고_내림차순이다() {

        VideoSearchCond cond = new VideoSearchCond();

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        LocalDateTime globalLatest = repository.findAll().stream()
                .map(Video::createdAt)
                .max(LocalDateTime::compareTo)
                .orElseThrow();
        assertThat(list.content().getFirst().createdAt()).isEqualTo(globalLatest);

        for (int i = 1; i < list.content().size(); i++) {
            assertThat(list.content().get(i - 1).createdAt())
                    .isAfterOrEqualTo(list.content().get(i).createdAt());
        }
    }

    @Test
    void sort_oldest는_전체에서_가장_오래된_영상이_맨앞이고_오름차순이다() {

        VideoSearchCond cond = new VideoSearchCond();
        cond.setSort("oldest");

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        LocalDateTime globalOldest = repository.findAll().stream()
                .map(Video::createdAt)
                .min(LocalDateTime::compareTo)
                .orElseThrow();
        assertThat(list.content().getFirst().createdAt()).isEqualTo(globalOldest);

        for (int i = 1; i < list.content().size(); i++) {
            assertThat(list.content().get(i - 1).createdAt())
                    .isBeforeOrEqualTo(list.content().get(i).createdAt());
        }
    }

    @Test
    void 초과_페이지는_예외없이_빈_content와_전체_totalCount를_반환한다() {

        VideoSearchCond cond = new VideoSearchCond();
        cond.setPage(99);

        VideoPageResponse list = videoListService.list(cond.getPage(), cond.getSize(), cond.getSort());

        assertThat(list.content()).isEmpty();
        assertThat(list.page()).isEqualTo(99);
        assertThat(list.totalCount()).isEqualTo(23);
    }

    @Test
    void page_size_조합은_정확히_그_구간만_반환한다() {

        // 개수만 세지 않고, 전체를 latest 순으로 정렬했을 때의 id 목록과 각 페이지 슬라이스를 비교한다
        List<Long> expectedLatestIds = repository.findAll().stream()
                .sorted(Comparator.comparing(Video::createdAt).reversed())
                .map(Video::id)
                .toList();

        for (int page = 0; page <= 2; page++) {
            VideoPageResponse list = videoListService.list(page, 10, "latest");

            List<Long> pageIds = list.content().stream()
                    .map(VideoResponse::id)
                    .toList();

            int from = page * 10;
            int to = Math.min(from + 10, expectedLatestIds.size());
            assertThat(pageIds).containsExactlyElementsOf(expectedLatestIds.subList(from, to));
        }
    }
}