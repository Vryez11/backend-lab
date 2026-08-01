package com.vryez.backendlab.lab09;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@Import(CommentRepository.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentApiAcceptanceTest {

    @Autowired MockMvc mvc;

    @Test @Order(1)
    @DisplayName("AT1: 파라미터 없이 호출해도 200 + 기본값(page=0, size=10, latest)이 적용된다")
    void at1_noParams() throws Exception {
        mvc.perform(get("/videos/1/comments"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(10))
           .andExpect(jsonPath("$.sort").value("latest"))
           .andExpect(jsonPath("$.totalCount").value(12))
           .andExpect(jsonPath("$.comments.length()").value(10))
           .andExpect(jsonPath("$.comments[0].id").value(12));
    }

    @Test @Order(2)
    @DisplayName("AT2: page=1&size=5&sort=oldest → 2페이지 5건, 오래된 순(첫 요소 id=6)")
    void at2_pagingAndOldestSort() throws Exception {
        mvc.perform(get("/videos/1/comments")
                .param("page", "1").param("size", "5").param("sort", "oldest"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.page").value(1))
           .andExpect(jsonPath("$.size").value(5))
           .andExpect(jsonPath("$.comments.length()").value(5))
           .andExpect(jsonPath("$.comments[0].id").value(6))
           .andExpect(jsonPath("$.comments[4].id").value(10));
    }

    @Test @Order(3)
    @DisplayName("AT3: keyword=질문 → 3건(id 2,5,9)만, 나머지 파라미터는 기본값 유지(latest 순 9,5,2)")
    void at3_keywordFilter() throws Exception {
        mvc.perform(get("/videos/1/comments").param("keyword", "질문"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.page").value(0))
           .andExpect(jsonPath("$.size").value(10))
           .andExpect(jsonPath("$.sort").value("latest"))
           .andExpect(jsonPath("$.totalCount").value(3))
           .andExpect(jsonPath("$.comments.length()").value(3))
           .andExpect(jsonPath("$.comments[0].id").value(9))
           .andExpect(jsonPath("$.comments[1].id").value(5))
           .andExpect(jsonPath("$.comments[2].id").value(2));
    }

    @Test @Order(4)
    @DisplayName("AT4: size=abc 타입 불일치는 500이 아니라 400")
    void at4_typeMismatchIs400() throws Exception {
        mvc.perform(get("/videos/1/comments").param("size", "abc"))
           .andExpect(status().isBadRequest());
    }

    @Test @Order(5)
    @DisplayName("AT5: 폼 POST → 201 + 생성 댓글(id=13), 이후 목록에 반영(totalCount=13, 최신 첫 요소)")
    void at5_createByForm() throws Exception {
        mvc.perform(post("/videos/1/comments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("author", "neo").param("content", "좋은 강의"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(13))
           .andExpect(jsonPath("$.author").value("neo"))
           .andExpect(jsonPath("$.content").value("좋은 강의"))
           .andExpect(jsonPath("$.createdAt").exists());

        mvc.perform(get("/videos/1/comments"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.totalCount").value(13))
           .andExpect(jsonPath("$.comments[0].id").value(13));
    }
}
