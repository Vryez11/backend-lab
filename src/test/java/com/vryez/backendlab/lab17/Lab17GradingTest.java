package com.vryez.backendlab.lab17;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab17GradingTest {

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        UserContextHolder.clear();
    }

    @Test
    void T1_로그인_요청_직후_같은_스레드의_비로그인_요청은_401() throws Exception {
        mockMvc.perform(get("/api/lab/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("철수"));

        mockMvc.perform(get("/api/lab/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void T2_미존재_사용자_요청_뒤에도_직전_신원이_새지_않는다() throws Exception {
        mockMvc.perform(get("/api/lab/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("철수"));

        mockMvc.perform(get("/api/lab/me").header("X-User-Id", "999"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void T3_같은_워커_스레드를_재사용해도_다음_요청에_신원이_새지_않는다() throws Exception {
        ExecutorService worker = Executors.newSingleThreadExecutor();
        try {
            worker.submit(() -> mockMvc.perform(get("/api/lab/me").header("X-User-Id", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nickname").value("철수"))
                    .andReturn()).get();

            worker.submit(() -> mockMvc.perform(get("/api/lab/me"))
                    .andExpect(status().isUnauthorized())
                    .andReturn()).get();
        } finally {
            worker.shutdownNow();
        }
    }

    @Test
    void T4_처리_중_예외가_난_요청_뒤에도_신원이_새지_않는다() throws Exception {
        try {
            mockMvc.perform(get("/api/lab17/boom").header("X-User-Id", "1"));
        } catch (Exception expected) {
            // 인증까지 통과한 요청이 처리 중 폭발하는 상황을 재현
        }

        mockMvc.perform(get("/api/lab/me"))
                .andExpect(status().isUnauthorized());
    }

    @TestConfiguration
    static class BoomConfig {

        // @RestController를 붙이면 테스트 클래스패스 컴포넌트 스캔에 걸려 @Bean 등록과 중복된다.
        // @RequestMapping은 스테레오타입이 아니라 스캔되지 않고, 핸들러 감지 조건은 만족한다.
        @RequestMapping
        static class BoomController {
            @GetMapping("/api/lab17/boom")
            @ResponseBody
            String boom() {
                throw new IllegalStateException("boom");
            }
        }

        @Bean
        BoomController lab17BoomController() {
            return new BoomController();
        }
    }
}
