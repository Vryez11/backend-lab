package com.vryez.backendlab.lab14;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
@Import({VideoListService.class, VideoRepository.class})
class VideoApiAcceptanceTest {

    @Autowired MockMvc mvc;

    /**
     * 시드 규칙(id 1~23, hourOffset=(id*13)%23, views=((id*7)%23)*100+id)을
     * 구현과 무관하게 재계산한 독립 오라클.
     */
    record Seed(long id, long hourOffset, long views) {
        static List<Seed> all() {
            List<Seed> s = new ArrayList<>();
            for (long id = 1; id <= 23; id++) {
                s.add(new Seed(id, (id * 13) % 23, ((id * 7) % 23) * 100 + id));
            }
            return s;
        }
    }

    private static List<Long> idsSortedBy(Comparator<Seed> cmp) {
        return Seed.all().stream().sorted(cmp).map(Seed::id).toList();
    }

    private static final List<Long> LATEST = idsSortedBy(Comparator.comparingLong(Seed::hourOffset).reversed());
    private static final List<Long> OLDEST = idsSortedBy(Comparator.comparingLong(Seed::hourOffset));
    private static final List<Long> VIEWS = idsSortedBy(Comparator.comparingLong(Seed::views).reversed());

    /** content의 id 순서가 기대 목록+과 정확히 일치하는지 원소 단위로 단언한다. */
    private void expectContentIds(ResultActions actions, List<Long> expectedIds) throws Exception {
        actions.andExpect(jsonPath("$.content").isArray())
               .andExpect(jsonPath("$.content.length()").value(expectedIds.size()));
        for (int i = 0; i < expectedIds.size(); i++) {
            actions.andExpect(jsonPath("$.content[" + i + "].id").value(expectedIds.get(i)));
        }
    }

    @Test
    @DisplayName("AT1: 파라미터 없이 → 200, page=0/size=10/totalCount=23, 최신순 상위 10개가 참조 정렬과 정확히 일치")
    void at1_noParams_defaultsAndLatestOrder() throws Exception {
        ResultActions actions = mvc.perform(get("/lab14/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalCount").value(23))
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").exists())
                .andExpect(jsonPath("$.content[0].views").exists())
                .andExpect(jsonPath("$.content[0].createdAt").exists());

        expectContentIds(actions, LATEST.subList(0, 10));
    }

    @Test
    @DisplayName("AT2: page=1&size=10 → 최신순 11~20번째 윈도우가 정확히 반환된다")
    void at2_middlePage_exactWindow() throws Exception {
        expectContentIds(
                mvc.perform(get("/lab14/videos").param("page", "1").param("size", "10"))
                   .andExpect(status().isOk()),
                LATEST.subList(10, 20));
    }

    @Test
    @DisplayName("AT3: 마지막 페이지 page=2&size=10 → 남은 3개만, 예외 없음, totalCount 유지")
    void at3_lastPage_remainderOnly() throws Exception {
        expectContentIds(
                mvc.perform(get("/lab14/videos").param("page", "2"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.totalCount").value(23)),
                LATEST.subList(20, 23));
    }

    @Test
    @DisplayName("AT4: sort=oldest → 참조 오름차순과 정확히 일치(첫 원소가 전체 최소)")
    void at4_oldest_exactOrder() throws Exception {
        expectContentIds(
                mvc.perform(get("/lab14/videos").param("sort", "oldest"))
                   .andExpect(status().isOk()),
                OLDEST.subList(0, 10));
    }

    @Test
    @DisplayName("AT5: sort=views → 참조 조회수 내림차순과 정확히 일치(첫 원소가 전체 최대 2213뷰)")
    void at5_views_exactOrder() throws Exception {
        expectContentIds(
                mvc.perform(get("/lab14/videos").param("sort", "views"))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.content[0].views").value(2213)),
                VIEWS.subList(0, 10));
    }

    @Test
    @DisplayName("AT6: 초과 페이지 page=99 → 200, content 빈 배열, totalCount=23")
    void at6_overflowPage_emptyNoError() throws Exception {
        mvc.perform(get("/lab14/videos").param("page", "99"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content.length()").value(0))
           .andExpect(jsonPath("$.totalCount").value(23));
    }
}
