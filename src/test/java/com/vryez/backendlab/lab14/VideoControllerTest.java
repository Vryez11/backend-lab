package com.vryez.backendlab.lab14;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
@Import({VideoListService.class, VideoRepository.class})
class VideoControllerTest {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("파라미터 없이 GET /lab14/videos → 200, 기본값 바인딩(page=0, size=10, latest)과 JSON 구조")
    void noParams_defaultsAppliedByBinding() throws Exception {
        mvc.perform(get("/lab14/videos"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(10))
           .andExpect(jsonPath("$.totalCount").value(23))
           .andExpect(jsonPath("$.content.length()").value(10))
           // latest 기본 정렬: 전체에서 가장 최신인 id=7이 맨 앞
           .andExpect(jsonPath("$.content[0].id").value(7))
           .andExpect(jsonPath("$.content[0].title").exists())
           .andExpect(jsonPath("$.content[0].views").exists())
           .andExpect(jsonPath("$.content[0].createdAt").exists());
    }

    @Test
    @DisplayName("sort=oldest → 가장 오래된 영상(id=23)이 content[0]")
    void sortOldest_globalOldestFirst() throws Exception {
        mvc.perform(get("/lab14/videos").param("sort", "oldest"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].id").value(23));
    }

    @Test
    @DisplayName("sort=views → 조회수가 전체에서 가장 큰 영상(id=13, 2213뷰)이 content[0]")
    void sortViews_globalMaxFirst() throws Exception {
        mvc.perform(get("/lab14/videos").param("sort", "views"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].id").value(13))
           .andExpect(jsonPath("$.content[0].views").value(2213));
    }

    @Test
    @DisplayName("마지막 페이지 page=2 → 남은 3개만 반환, totalCount는 그대로")
    void lastPage_returnsRemainderOnly() throws Exception {
        mvc.perform(get("/lab14/videos").param("page", "2"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.page").value(2))
           .andExpect(jsonPath("$.content.length()").value(3))
           .andExpect(jsonPath("$.totalCount").value(23));
    }

    @Test
    @DisplayName("초과 페이지 page=99 → 200, content 빈 배열, totalCount는 전체 개수 그대로")
    void overflowPage_emptyContentNoError() throws Exception {
        mvc.perform(get("/lab14/videos").param("page", "99"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content.length()").value(0))
           .andExpect(jsonPath("$.totalCount").value(23));
    }
}
