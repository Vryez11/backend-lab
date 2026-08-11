package com.vryez.backendlab.lab18;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TimecodeResumeTest {

    @Autowired MockMvc mvc;

    @Test
    void 정상_변환_205초() throws Exception {
        mvc.perform(get("/lab18/resume").param("at", "03:25"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.seconds").value(205));
    }

    @Test
    void 영시영초_0초() throws Exception {
        mvc.perform(get("/lab18/resume").param("at", "00:00"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.seconds").value(0));
    }

    @Test
    void 분은_여러자리_허용() throws Exception {
        mvc.perform(get("/lab18/resume").param("at", "125:30"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.seconds").value(7530));
    }

    @Test
    void 초가_60이상이면_400() throws Exception {
        mvc.perform(get("/lab18/resume").param("at", "03:75"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void 초가_두자리가_아니면_400() throws Exception {
        mvc.perform(get("/lab18/resume").param("at", "3:5"))
           .andExpect(status().isBadRequest());
    }

    @Test
    void 숫자가_아니면_400() throws Exception {
        mvc.perform(get("/lab18/resume").param("at", "abc"))
           .andExpect(status().isBadRequest());
    }
}
