package com.vryez.backendlab.lab10;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 완료 조건 ④ — synchronized·AtomicLong·LongAdder 모두 결과는 정확하지만,
 * 같은 고경합 부하에서 처리 시간이 다름을 수치로 보인다.
 * 정밀 벤치마크(JMH)가 아니므로 절대치가 아니라 방향만 본다.
 */
class CounterThroughputComparisonTest {

    interface Counter {
        void add();

        long get();
    }

    static final class SynchronizedCounter implements Counter {
        private long total;

        @Override public synchronized void add() {
            total = total + 1;
        }

        @Override public synchronized long get() {
            return total;
        }
    }

    static final class AtomicCounter implements Counter {
        private final AtomicLong total = new AtomicLong();

        @Override public void add() {
            total.incrementAndGet();
        }

        @Override public long get() {
            return total.get();
        }
    }

    static final class AdderCounter implements Counter {
        private final LongAdder total = new LongAdder();

        @Override public void add() {
            total.increment();
        }

        @Override public long get() {
            return total.sum();
        }
    }

    @Test
    void 세_구현_모두_정확하지만_고경합_처리_시간은_다르다() throws Exception {
        int threads = 200, perThread = 5_000;

        measure("synchronized", SynchronizedCounter::new, threads, perThread);
        measure("AtomicLong  ", AtomicCounter::new, threads, perThread);
        measure("LongAdder   ", AdderCounter::new, threads, perThread);
    }

    private void measure(String label, java.util.function.Supplier<Counter> factory,
                         int threads, int perThread) throws Exception {
        long expected = (long) threads * perThread;

        // JIT 워밍업 1회(측정 제외) 후 3회 측정
        runOnce(factory.get(), threads, perThread, expected);

        long[] elapsedMillis = new long[3];
        for (int round = 0; round < 3; round++) {
            Counter counter = factory.get();
            long begin = System.nanoTime();
            runOnce(counter, threads, perThread, expected);
            elapsedMillis[round] = (System.nanoTime() - begin) / 1_000_000;
        }
        System.out.printf("%s : %d ms / %d ms / %d ms%n",
                label, elapsedMillis[0], elapsedMillis[1], elapsedMillis[2]);
    }

    private void runOnce(Counter counter, int threads, int perThread, long expected) throws Exception {
        ConcurrentLoad.run(threads, perThread, counter::add);
        assertThat(counter.get()).isEqualTo(expected);
    }
}
