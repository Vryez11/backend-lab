package com.vryez.backendlab.lab21;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab21VideoUploadAcceptanceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    VideoRepository repository;

    private String json(String title, String handle, String category, String durationSecRaw) {
        return """
                {"title": %s, "handle": %s, "category": %s, "durationSec": %s}
                """.formatted(quote(title), quote(handle), quote(category), durationSecRaw);
    }

    private String quote(String v) {
        return v == null ? "null" : "\"" + v + "\"";
    }

    private void expectRejectedWithoutInsert(String body) throws Exception {
        int before = repository.count();
        mockMvc.perform(post("/lab21/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
        assertThat(repository.count()).as("거부된 요청은 행을 저장하면 안 된다").isEqualTo(before);
    }

    @Test
    @DisplayName("A. 정상 업로드 — 201 + id 반환 + 행 1 증가")
    void 정상_업로드() throws Exception {
        int before = repository.count();
        mockMvc.perform(post("/lab21/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("오버워치 랭크 하이라이트", "ow-rank-hl", "GAME", "735")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.handle").value("ow-rank-hl"));
        assertThat(repository.count()).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("B. title 공백/빈 문자열 — 400, 행 불변")
    void title_공백_거부() throws Exception {
        expectRejectedWithoutInsert(json("   ", "blank-title-1", "GAME", "100"));
        expectRejectedWithoutInsert(json("", "blank-title-2", "GAME", "100"));
    }

    @Test
    @DisplayName("C. handle 경계 — 대문자/시작 하이픈/끝 하이픈/2자 400, good-handle-1 통과")
    void handle_경계() throws Exception {
        expectRejectedWithoutInsert(json("제목", "My-Video", "GAME", "100"));
        expectRejectedWithoutInsert(json("제목", "-abc", "GAME", "100"));
        expectRejectedWithoutInsert(json("제목", "abc-", "GAME", "100"));
        expectRejectedWithoutInsert(json("제목", "ab", "GAME", "100"));

        mockMvc.perform(post("/lab21/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("제목", "good-handle-1", "GAME", "100")))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("D. category — 목록 밖(FOOD) 400, MUSIC 통과")
    void category_검증() throws Exception {
        expectRejectedWithoutInsert(json("제목", "food-cat-1", "FOOD", "100"));

        mockMvc.perform(post("/lab21/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("제목", "music-cat-1", "MUSIC", "100")))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("E. durationSec — 0/-5/50000/null 전부 400, 행 불변")
    void durationSec_범위() throws Exception {
        expectRejectedWithoutInsert(json("제목", "dur-zero-1", "GAME", "0"));
        expectRejectedWithoutInsert(json("제목", "dur-neg-1", "GAME", "-5"));
        expectRejectedWithoutInsert(json("제목", "dur-over-1", "GAME", "50000"));
        expectRejectedWithoutInsert(json("제목", "dur-null-1", "GAME", "null"));
    }

    @Test
    @DisplayName("F. 바디 변환 실패 — durationSec에 문자열 \"abc\" → 400, 행 불변")
    void 바디_변환_실패() throws Exception {
        expectRejectedWithoutInsert(json("제목", "dur-abc-1", "GAME", "\"abc\""));
    }
}
