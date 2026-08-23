package com.vryez.backendlab.lab23;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab23AcceptanceTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("T1. 정상 조회 — 200, id=1, title 존재")
    void 정상_조회() throws Exception {
        mockMvc.perform(get("/lab23/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").isNotEmpty());
    }

    @Test
    @DisplayName("T2. 없는 영상 — 404, VIDEO_NOT_FOUND, fieldErrors=[], 스프링 기본 필드 없음")
    void 없는_영상_404() throws Exception {
        mockMvc.perform(get("/lab23/videos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("VIDEO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors").isEmpty())
                .andExpect(jsonPath("$.timestamp").doesNotExist())
                .andExpect(jsonPath("$.path").doesNotExist())
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist());
    }

    @Test
    @DisplayName("T3. 경로 변수 타입 불일치 — 400, TYPE_MISMATCH")
    void 타입_불일치_400() throws Exception {
        mockMvc.perform(get("/lab23/videos/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TYPE_MISMATCH"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("T4. 검증 실패(복수 필드) — 400, VALIDATION_ERROR, title·durationSeconds 모두 포함")
    void 검증_실패_복수_필드() throws Exception {
        mockMvc.perform(post("/lab23/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"","durationSeconds":-5,"isPublic":true}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[*].field",
                        hasItems("title", "durationSeconds")));
    }

    @Test
    @DisplayName("T5. 깨진 JSON — 400, MALFORMED_REQUEST")
    void 깨진_JSON_400() throws Exception {
        mockMvc.perform(post("/lab23/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("T6. 예상치 못한 서버 오류 — 500, INTERNAL_ERROR, 내부 메시지 미노출")
    void 서버_오류_fallback() throws Exception {
        mockMvc.perform(get("/lab23/videos/0"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(content().string(not(containsString("stub storage failure"))))
                .andExpect(content().string(not(containsString("IllegalStateException"))));
    }

    @Test
    @DisplayName("T7. 정상 생성 회귀 — 201")
    void 정상_생성_회귀() throws Exception {
        mockMvc.perform(post("/lab23/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"스프링 MVC 정복","durationSeconds":900,"isPublic":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("스프링 MVC 정복"));
    }
}
