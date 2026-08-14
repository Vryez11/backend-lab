package com.vryez.backendlab.lab19;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.cdn-base-url=https://cdn.other.com")
@AutoConfigureMockMvc
class Lab19CdnOverrideGradingTest {

    @Autowired MockMvc mvc;

    @Test
    @DisplayName("AT2: CDN base 설정값을 바꾸면 응답의 thumbnailUrl도 그대로 따라간다 (하드코딩 방지)")
    void at2_changedCdnBaseIsReflected() throws Exception {
        ResultActions actions = mvc.perform(get("/lab19/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        for (int i = 0; i < 3; i++) {
            actions.andExpect(jsonPath("$[" + i + "].thumbnailUrl", startsWith("https://cdn.other.com/th/")));
        }
        actions.andExpect(jsonPath("$[0].thumbnailUrl").value("https://cdn.other.com/th/v1001.jpg"));
    }
}
