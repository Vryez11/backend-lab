package com.vryez.backendlab.lab17;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserContextTest {

    @Autowired
    MockMvc mockMvc;
    @Test
    void T1_UserContext가_다음_요청에도_그대로_있는가() throws Exception {

        // 헤더가 없으면 인증이 되면 안됨.
        mockMvc.perform
                        (get("/api/lab/me"))
                .andExpect(status().isUnauthorized());

        // 헤더가 있으면 인증이 됨.
        mockMvc.perform(get("/api/lab/me").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("철수"));

        // 헤더가 없으면 인증이 되면 안됨.
        mockMvc.perform(get("/api/lab/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void T2_미존재_사용자_신원이_절대_새어_나오면_안된다() throws Exception {

        // 헤더에 미존재 사용자 신원 전달.
        mockMvc.perform(get("/api/lab/me").header("X-User-Id", "999"))
                .andExpect(status().isUnauthorized());

        // 다음 헤더가 없는 요청에 미존재 신원이 전달 되면 안됨
        mockMvc.perform(get("/api/lab/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.nickname").doesNotExist());
    }
}
