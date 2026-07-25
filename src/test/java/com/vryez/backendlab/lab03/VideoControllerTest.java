package com.vryez.backendlab.lab03;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VideoController.class)
class VideoControllerTest {

    @Autowired MockMvc mvc;

    @Test @DisplayName("keyword로 제목을 검색하면 매칭 동영상을 JSON 배열로 반환한다")
    void searchByKeyword() throws Exception {
        mvc.perform(get("/api/videos").param("keyword", "spring"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3))
           .andExpect(jsonPath("$[0].id").exists())
           .andExpect(jsonPath("$[0].title").exists());
    }

    @Test @DisplayName("검색은 대소문자를 무시한다")
    void searchCaseInsensitive() throws Exception {
        mvc.perform(get("/api/videos").param("keyword", "SPRING"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3));
    }

    @Test @DisplayName("limit을 생략하면 기본값 10이 적용된다")
    void limitDefault() throws Exception {
        mvc.perform(get("/api/videos").param("keyword", "spring"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(3));
    }

    @Test @DisplayName("limit을 지정하면 그 개수만큼만 반환한다")
    void limitApplied() throws Exception {
        mvc.perform(get("/api/videos").param("keyword", "spring").param("limit", "2"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2));
    }

    @Test @DisplayName("keyword가 없으면 400을 반환한다(필수 파라미터)")
    void keywordRequired() throws Exception {
        mvc.perform(get("/api/videos"))
           .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("매칭이 없으면 200과 빈 배열을 반환한다")
    void noMatch() throws Exception {
        mvc.perform(get("/api/videos").param("keyword", "no-such-xyz"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(0));
    }

    @Test @DisplayName("폼으로 등록하면 201과 생성 리소스(id 포함)를 반환한다")
    void createByForm() throws Exception {
        mvc.perform(post("/api/videos")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Kafka 입문")
                .param("uploader", "vryez")
                .param("durationSec", "620"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").exists())
           .andExpect(jsonPath("$.title").value("Kafka 입문"))
           .andExpect(jsonPath("$.uploader").value("vryez"))
           .andExpect(jsonPath("$.durationSec").value(620));
    }

    @Test @DisplayName("등록한 동영상은 이후 검색으로 조회된다")
    void createThenSearch() throws Exception {
        mvc.perform(post("/api/videos")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", "Redis 캐시 전략")
                .param("uploader", "vryez")
                .param("durationSec", "740"))
           .andExpect(status().isCreated());

        mvc.perform(get("/api/videos").param("keyword", "Redis"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.title == 'Redis 캐시 전략')]").exists());
    }
}
