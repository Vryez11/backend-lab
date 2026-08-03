package com.vryez.backendlab.lab10;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 완료 조건 ② — volatile은 가시성만 보장할 뿐 복합 연산(읽기→+1→쓰기)의
 * 원자성을 보장하지 않으므로, 증가 유실(lost update)을 막지 못함을 증명한다.
 */
class VolatileCounterTest {

    static final class VolatileCounter {
        private volatile long total;

        void add() {
            total = total + 1;
        }

        long get() {
            return total;
        }
    }

    @Test
    void volatile은_가시성만_보장해_증가_유실을_막지_못한다() throws Exception {
        int threads = 100, perThread = 10_000;
        long expected = (long) threads * perThread;

        // 유실은 확률적 현상이라 최대 3회까지 시도해 한 번이라도 관측되면 증명 성립으로 본다.
        long observedLoss = -1;
        for (int attempt = 0; attempt < 3 && observedLoss < 0; attempt++) {
            VolatileCounter counter = new VolatileCounter();
            ConcurrentLoad.run(threads, perThread, counter::add);
            if (counter.get() < expected) {
                observedLoss = expected - counter.get();
            }
        }

        assertThat(observedLoss)
                .as("volatile 카운터가 3회 시도에서 단 한 번도 유실되지 않았다 — 경합 조건을 의심하라")
                .isPositive();
    }
}
