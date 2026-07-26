package com.vryez.backendlab.lab04;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WatchPointServiceConcurrencyTest {

    @Autowired
    WatchPointService watchPointService;

    @Test
    void 동시_시청기록_시_각_응답은_자기_요청의_동영상과_포인트여야_한다() throws Exception {
        int taskCount = 100;
        ExecutorService pool = Executors.newFixedThreadPool(taskCount);
        CountDownLatch ready = new CountDownLatch(taskCount);
        CountDownLatch start = new CountDownLatch(1);

        List<WatchRequest> requests = new ArrayList<>();
        for (int i = 1; i <= taskCount; i++) {
            requests.add(new WatchRequest("user-" + i, "video-" + i, i * 10L));
        }

        List<Future<WatchResponse>> futures = new ArrayList<>();
        for (WatchRequest request : requests) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await();
                return watchPointService.record(request.userId(), request.videoId(), request.watchedSeconds());
            }));
        }

        ready.await();
        start.countDown();

        int mismatches = 0;
        for (int i = 0; i < taskCount; i++) {
            WatchRequest request = requests.get(i);
            WatchResponse response = futures.get(i).get();
            long expectedPoints = request.watchedSeconds() / 10;
            boolean matched = response.userId().equals(request.userId())
                    && response.videoId().equals(request.videoId())
                    && response.earnedPoints() == expectedPoints;
            if (!matched) {
                mismatches++;
            }
        }
        pool.shutdown();

        assertThat(mismatches).isZero();
    }

    @Test
    void 동시_100건_전체_처리시간은_SLA_300ms_이내여야_한다() throws Exception {
        int taskCount = 100;
        ExecutorService pool = Executors.newFixedThreadPool(taskCount);
        CountDownLatch ready = new CountDownLatch(taskCount);
        CountDownLatch start = new CountDownLatch(1);

        List<Callable<WatchResponse>> tasks = new ArrayList<>();
        for (int i = 1; i <= taskCount; i++) {
            long watchedSeconds = i * 10L;
            String userId = "user-" + i;
            String videoId = "video-" + i;
            tasks.add(() -> {
                ready.countDown();
                start.await();
                return watchPointService.record(userId, videoId, watchedSeconds);
            });
        }

        List<Future<WatchResponse>> futures = new ArrayList<>();
        for (Callable<WatchResponse> task : tasks) {
            futures.add(pool.submit(task));
        }

        ready.await();
        long begin = System.nanoTime();
        start.countDown();
        for (Future<WatchResponse> future : futures) {
            future.get();
        }
        long elapsedMs = (System.nanoTime() - begin) / 1_000_000;
        pool.shutdown();

        assertThat(elapsedMs).isLessThan(300);
    }
}
