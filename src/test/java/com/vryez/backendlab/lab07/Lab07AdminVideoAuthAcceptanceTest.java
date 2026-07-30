package com.vryez.backendlab.lab07;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class Lab07AdminVideoAuthAcceptanceTest {

    @Autowired
    MockMvc mvc;

    private MockHttpSession loginAs(String loginId, String password) throws Exception {
        MvcResult result = mvc.perform(post("/lab07/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginId\":\"%s\",\"password\":\"%s\"}".formatted(loginId, password)))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    @DisplayName("1. 비로그인 사용자의 삭제 요청은 401로 거부되고 영상은 남아 있다")
    void deleteWithoutLogin_rejected401() throws Exception {
        mvc.perform(delete("/lab07/admin/videos/1"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/lab07/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)));
    }

    @Test
    @DisplayName("2. 일반 회원(USER)이 삭제 API를 직접 호출하면 403으로 거부되고 영상은 남아 있다")
    void deleteAsUser_rejected403() throws Exception {
        MockHttpSession session = loginAs("viewer", "viewer1234");

        mvc.perform(delete("/lab07/admin/videos/1").session(session))
                .andExpect(status().isForbidden());

        mvc.perform(get("/lab07/videos").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 1)]", hasSize(1)));
    }

    @Test
    @DisplayName("3. 관리자(ADMIN)는 삭제 API로 영상을 정상 삭제한다")
    void deleteAsAdmin_succeeds200() throws Exception {
        MockHttpSession session = loginAs("admin", "admin1234");

        mvc.perform(delete("/lab07/admin/videos/2").session(session))
                .andExpect(status().isOk());

        mvc.perform(get("/lab07/videos").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == 2)]", hasSize(0)));
    }

    @Test
    @DisplayName("4. canDelete 힌트는 관리자에게만 true다")
    void canDeleteHint_reflectsRole() throws Exception {
        MockHttpSession viewerSession = loginAs("viewer", "viewer1234");
        mvc.perform(get("/lab07/videos").session(viewerSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].canDelete", everyItem(is(false))));

        MockHttpSession adminSession = loginAs("admin", "admin1234");
        mvc.perform(get("/lab07/videos").session(adminSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].canDelete", everyItem(is(true))));
    }
}
