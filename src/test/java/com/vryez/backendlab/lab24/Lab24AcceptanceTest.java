package com.vryez.backendlab.lab24;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab24AcceptanceTest {

    @Autowired
    MockMvc mockMvc;

    private static final String VALID_BODY = """
            {"author":"kim","content":"좋은 영상"}
            """;

    @Test
    @DisplayName("T1. 정상 등록 — 201, id>0, author·content 존재")
    void 정상_등록() throws Exception {
        mockMvc.perform(post("/lab24/videos/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(greaterThan(0)))
                .andExpect(jsonPath("$.author").value("kim"))
                .andExpect(jsonPath("$.content").value("좋은 영상"));
    }

    @Test
    @DisplayName("T2. 검증 실패(복수 필드) — 400, VALIDATION_ERROR, errors에 author·content 2건")
    void 검증_실패_복수_필드() throws Exception {
        String badBody = """
                {"author":"%s","content":""}
                """.formatted("a".repeat(31));

        mockMvc.perform(post("/lab24/videos/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors[*].field", hasItems("author", "content")))
                .andExpect(jsonPath("$.errors[*].reason").exists());
    }

    @Test
    @DisplayName("T3. 깨진 JSON — 400, MALFORMED_JSON, errors 빈 배열")
    void 깨진_JSON() throws Exception {
        mockMvc.perform(post("/lab24/videos/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"author\":\"kim\",\"content\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("T4. 경로변수 타입 불일치 — 400, TYPE_MISMATCH, errors 빈 배열")
    void 타입_불일치() throws Exception {
        mockMvc.perform(post("/lab24/videos/abc/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("TYPE_MISMATCH"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("T5. 없는 영상 — 404, VIDEO_NOT_FOUND, errors 빈 배열 (500이면 실패)")
    void 없는_영상() throws Exception {
        mockMvc.perform(post("/lab24/videos/99/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("VIDEO_NOT_FOUND"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("T6. 댓글 잠긴 영상 — 409, COMMENTS_DISABLED, errors 빈 배열")
    void 잠긴_영상() throws Exception {
        mockMvc.perform(post("/lab24/videos/3/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("COMMENTS_DISABLED"))
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}
