package com.vryez.backendlab.lab10;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

final class ConcurrentLoad {

    private ConcurrentLoad() {
    }

    /**
     * threads개의 스레드가 동시에 출발해 각자 task를 perThread번 실행하고, 전부 끝날 때까지 기다린다.
     */
    static void run(int threads, int perThread, Runnable task) throws InterruptedException {
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            CountDownLatch ready = new CountDownLatch(threads);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(threads);

            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                    for (int i = 0; i < perThread; i++) {
                        task.run();
                    }
                    done.countDown();
                });
            }
            ready.await();
            start.countDown();
            if (!done.await(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
                throw new AssertionError("동시 부하가 30초 안에 끝나지 않았다 — 데드락 또는 작업 중 예외를 의심하라");
            }
        }
    }
}
