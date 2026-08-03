package com.vryez.backendlab.lab10;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Component
public class HeartCounter {

    private final LongAdder total = new LongAdder();

    public void add() {
        total.increment();
    }

    // sum()과 reset()은 방송 종료 후처럼 증가가 멈춘 시점에 호출하는 것을 전제로 한다.
    public long get() {
        return total.sum();
    }

    public void reset() {
        total.reset();
    }
}
