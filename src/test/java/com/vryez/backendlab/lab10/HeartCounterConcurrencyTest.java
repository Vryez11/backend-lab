package com.vryez.backendlab.lab10;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HeartCounterConcurrencyTest {

    @Autowired HeartCounter counter;

    @Test
    void 백만개의_하트를_동시에_쏘면_정확히_백만이어야_한다() throws Exception {
        int threads = 100, perThread = 10_000;
        counter.reset();

        ConcurrentLoad.run(threads, perThread, counter::add);

        assertThat(counter.get()).isEqualTo((long) threads * perThread);
    }

    @RepeatedTest(3)
    void 경합을_더_키워_반복_실행해도_유실이_없어야_한다() throws Exception {
        int threads = 200, perThread = 5_000;
        counter.reset();

        ConcurrentLoad.run(threads, perThread, counter::add);

        assertThat(counter.get()).isEqualTo((long) threads * perThread);
    }
}
