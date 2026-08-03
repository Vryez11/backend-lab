package com.vryez.backendlab.lab10;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class HeartCounter {

//    private long total;

//    private volatile long total;

//    private long total;

    private AtomicLong total = new AtomicLong(0L);

    public void add() {
        total.getAndIncrement();
    }

    public long get() {
        return total.get();
    }

    public void reset() {
        total = new AtomicLong(0L);
    }
}
