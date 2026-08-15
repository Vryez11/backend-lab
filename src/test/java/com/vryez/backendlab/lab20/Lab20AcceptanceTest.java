package com.vryez.backendlab.lab20;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * lab20 인수 테스트. 워커 스레드를 1개로 고정해 스레드 재사용을 강제한다 —
 * 운영에서 간헐적으로만 보이던 증상이 여기서는 결정적으로 재현되어야 한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "server.tomcat.threads.max=1")
class Lab20AcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    AccessLogRepository accessLogRepository;

    @Autowired
    VideoService videoService;

    final TestRestTemplate restTemplate = new TestRestTemplate();

    @BeforeEach
    void setUp() {
        videoService.reset();
        accessLogRepository.clear();
    }

    @Test
    void T1_관리자_삭제_성공_시_접근로그_actor는_그_관리자다() {
        ResponseEntity<String> res = adminPost("/lab20/admin/videos/1/delete", "admin-1");

        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(accessLogRepository.findLast().actor()).isEqualTo("admin-1");
    }

    @Test
    void T2_삭제_성공_직후_공개_조회의_actor는_anonymous다() {
        adminPost("/lab20/admin/videos/1/delete", "admin-1");

        ResponseEntity<String> res = publicGet("/lab20/videos/2");

        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(accessLogRepository.findLast().actor()).isEqualTo("anonymous");
    }

    @Test
    void T3_없는_영상_삭제가_실패한_직후에도_공개_조회의_actor는_anonymous다() {
        ResponseEntity<String> deleteRes = adminPost("/lab20/admin/videos/999/delete", "admin-2");
        assertThat(deleteRes.getStatusCode().isError()).isTrue();

        ResponseEntity<String> res = publicGet("/lab20/videos/2");

        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(accessLogRepository.findLast().actor()).isEqualTo("anonymous");
    }

    @Test
    void T4_서로_다른_관리자가_연속_작업해도_각_접근로그의_actor는_각자_정확하다() {
        adminPost("/lab20/admin/videos/1/delete", "admin-1");
        adminPost("/lab20/admin/videos/999/delete", "admin-2");
        publicGet("/lab20/videos/2");

        List<AccessLog> logs = accessLogRepository.findAll();
        assertThat(logs).hasSize(3);
        assertThat(logs.get(0).actor()).isEqualTo("admin-1");
        assertThat(logs.get(1).actor()).isEqualTo("admin-2");
        assertThat(logs.get(2).actor()).isEqualTo("anonymous");
    }

    @Test
    void T5_공개_신고_요청의_actor도_anonymous다() {
        ResponseEntity<String> res = restTemplate.exchange(
                url("/lab20/videos/2/reports"), HttpMethod.POST, HttpEntity.EMPTY, String.class);

        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(accessLogRepository.findLast().actor()).isEqualTo("anonymous");
    }

    private ResponseEntity<String> adminPost(String path, String adminId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(RequestAuditFilter.ADMIN_HEADER, adminId);
        return restTemplate.exchange(url(path), HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }

    private ResponseEntity<String> publicGet(String path) {
        return restTemplate.getForEntity(url(path), String.class);
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }
}
