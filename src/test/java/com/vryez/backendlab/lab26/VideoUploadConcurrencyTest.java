package com.vryez.backendlab.lab26;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class VideoUploadConcurrencyTest {

    @Autowired
    private VideoUploadService videoUploadService;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void 단건_업로드에는_정상으로_응답() {

        String title = "안녕";
        Long UploaderId = 1L;

        UploadResponse response = videoUploadService.upload(new UploadRequest(title, UploaderId));

        Assertions.assertThat(response.title()).isEqualTo(title);
        Assertions.assertThat(response.uploaderId()).isEqualTo(UploaderId);
    }

    @Test
    void 동시_업로드_응답이_섞이지_않는다() throws Exception {
        int n = 20;
        ExecutorService executor = Executors.newFixedThreadPool(n);
        CountDownLatch startLine = new CountDownLatch(1);

        List<Future<UploadResponse>> futures = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            int id = i;   // 람다에서 쓰려면 사실상 final
            futures.add(executor.submit(() -> {
                try {
                    startLine.await();          // ← 출발선에서 대기
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // [빈칸 1] 고유값으로 요청 객체를 만들고
                UploadRequest request = new UploadRequest("video" + id, (long) id);
                // [빈칸 2] rest.postForObject(...)로 POST, UploadResponse.class로 받아서
                UploadResponse uploadResponse = rest.postForObject("/lab26/videos", request, UploadResponse.class);
                // [빈칸 3] 그 응답을 return
                return uploadResponse;
            }));
        }

        startLine.countDown();              // ← 일제히 출발

        for (int i = 1; i <= n; i++) {
            UploadResponse res = futures.get(i - 1).get(10, TimeUnit.SECONDS);

            assertThat(res.title()).isEqualTo("video" + i);
            assertThat(res.uploaderId()).isEqualTo(i);
        }

        executor.shutdown();
    }
}
