package com.vryez.backendlab.lab15;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * lab15 채점용 인수 테스트 — 싱글톤 빈 하나를 50개 플랫폼 스레드가 공유하며
 * 총 100,000건의 조회를 반영한 뒤 집계가 정확히 일치하는지 검증한다.
 */
@SpringBootTest
class Lab15AcceptanceTest {

    private static final int WORKERS = 100;
    private static final int CALLS_PER_WORKER = 1_000;

    @Autowired
    VideoViewService viewService;

    @Test
    void 동시_조회_십만건이_하나도_새지_않는다() throws InterruptedException {
        viewService.reset();

        ExecutorService pool = Executors.newFixedThreadPool(50);
        CountDownLatch done = new CountDownLatch(WORKERS);
        for (int w = 0; w < WORKERS; w++) {
            pool.submit(() -> {
                try {
                    for (int i = 0; i < CALLS_PER_WORKER; i++) {
                        viewService.view();
                    }
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(done.await(30, TimeUnit.SECONDS)).as("동시 부하 완료 대기").isTrue();
        pool.shutdown();

        assertThat(viewService.getViewCount()).isEqualTo((long) WORKERS * CALLS_PER_WORKER);
    }
}
