package com.vryez.backendlab.lab19;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab19GradingTest {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("AT1: 모든 thumbnailUrl이 설정된 CDN base로 시작하고, 어떤 값도 'null'을 포함하지 않는다")
    void at1_thumbnailUrlsUseConfiguredCdnBase() throws Exception {
        ResultActions actions = mvc.perform(get("/lab19/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        for (int i = 0; i < 3; i++) {
            actions.andExpect(jsonPath("$[" + i + "].thumbnailUrl", startsWith("https://cdn.example.com/th/")))
                   .andExpect(jsonPath("$[" + i + "].thumbnailUrl", not(containsString("null"))));
        }
        actions.andExpect(jsonPath("$[0].thumbnailUrl").value("https://cdn.example.com/th/v1001.jpg"))
               .andExpect(jsonPath("$[1].thumbnailUrl").value("https://cdn.example.com/th/v1002.jpg"))
               .andExpect(jsonPath("$[2].thumbnailUrl").value("https://cdn.example.com/th/v1003.jpg"));
    }

    @Test
    @DisplayName("AT3(회귀): 200 + 시드 3건 + 조회수 내림차순 정렬 유지")
    void at3_regression_seedAndOrderPreserved() throws Exception {
        mvc.perform(get("/lab19/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1001))
                .andExpect(jsonPath("$[0].viewCount").value(52000))
                .andExpect(jsonPath("$[1].id").value(1002))
                .andExpect(jsonPath("$[1].viewCount").value(13400))
                .andExpect(jsonPath("$[2].id").value(1003))
                .andExpect(jsonPath("$[2].viewCount").value(8700));
    }
}
