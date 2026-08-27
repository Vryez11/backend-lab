package com.vryez.backendlab.lab26;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class Lab26AcceptanceTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    void T2_단건_업로드는_ID가_부여되고_보낸_값이_그대로_반환된다() {
        UploadResponse res = rest.postForObject("/lab26/videos",
                new UploadRequest("acceptance-single", 42L), UploadResponse.class);

        assertThat(res.videoId()).isNotNull();
        assertThat(res.title()).isEqualTo("acceptance-single");
        assertThat(res.uploaderId()).isEqualTo(42L);
    }

    @Test
    void T1_동시_업로드_20건에서_어떤_응답에도_다른_요청의_값이_섞이지_않는다() throws Exception {
        int n = 20;
        ExecutorService executor = Executors.newFixedThreadPool(n);
        CountDownLatch startLine = new CountDownLatch(1);

        List<Future<UploadResponse>> futures = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            int id = i;
            futures.add(executor.submit(() -> {
                startLine.await();
                return rest.postForObject("/lab26/videos",
                        new UploadRequest("accept-vid-" + id, (long) id), UploadResponse.class);
            }));
        }

        startLine.countDown();

        for (int i = 1; i <= n; i++) {
            UploadResponse res = futures.get(i - 1).get(10, TimeUnit.SECONDS);
            assertThat(res.title()).isEqualTo("accept-vid-" + i);
            assertThat(res.uploaderId()).isEqualTo((long) i);
        }
        executor.shutdown();
    }
}
