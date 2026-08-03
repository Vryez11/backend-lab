package com.vryez.backendlab.lab10;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HeartCounterConcurrencyTest {

    @Autowired HeartCounter counter;

    @Test
    void 백만개의_하트를_동시에_쏘면_정확히_백만이어야_한다() throws Exception {
        int threads = 100, perThread = 10_000;
        counter.reset();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);

        for (int t = 0; t < threads; t++) {
            pool.submit(() -> {
                ready.countDown();
                try { start.await(); } catch (InterruptedException e) { throw new RuntimeException(e); }
                for (int i = 0; i < perThread; i++) counter.add();
                done.countDown();
            });
        }
        ready.await();
        start.countDown();
        done.await();
        pool.shutdown();

        assertThat(counter.get()).isEqualTo((long) threads * perThread);
    }
}
