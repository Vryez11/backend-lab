package com.vryez.backendlab.lab15;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

import static org.assertj.core.api.Assertions.assertThat;

class VideoViewControllerTest {

    private final VideoViewService viewService = new VideoViewService();

    @Test
    void 십만번_스레드_동시_시청() throws InterruptedException {
        int threads = 100_000;

        runConcurrently(threads, viewService::view);

        assertThat(viewService.getViewCount()).isEqualTo(100_000);
    }

    private void runConcurrently(int threads, Runnable task) throws InterruptedException {
        try (ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor()) {
            CountDownLatch ready = new CountDownLatch(threads);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(threads);

            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    ready.countDown();

                    try {
                        start.await();
                        task.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await();
            start.countDown();

            if (!done.await(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                throw new AssertionError("동시 부하가 30초 안에 끝나지 않았다");
            }
        }
    }
}