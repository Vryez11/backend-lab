package com.vryez.backendlab.lab01;

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
class OrderPriceServiceConcurrencyTest {

    @Autowired
    OrderPriceService orderPriceService;

    @Test
    void 동시_주문_시_각_응답_가격은_자기_주문금액의_90퍼센트여야_한다() throws Exception {
        int taskCount = 100;
        ExecutorService pool = Executors.newFixedThreadPool(taskCount);
        CountDownLatch ready = new CountDownLatch(taskCount);
        CountDownLatch start = new CountDownLatch(1);

        List<Callable<OrderResponse>> tasks = new ArrayList<>();
        for (int i = 1; i <= taskCount; i++) {
            long amount = i * 1000L;
            tasks.add(() -> {
                ready.countDown();
                start.await();
                return orderPriceService.order("user-" + amount, amount);
            });
        }

        List<Future<OrderResponse>> futures = new ArrayList<>();
        for (Callable<OrderResponse> t : tasks) {
            futures.add(pool.submit(t));
        }

        ready.await();
        start.countDown();

        int mismatches = 0;
        for (Future<OrderResponse> f : futures) {
            OrderResponse r = f.get();
            long amount = Long.parseLong(r.userId().substring("user-".length()));
            long expected = amount - (amount * 10 / 100);
            if (r.finalPrice() != expected) {
                mismatches++;
            }
        }
        pool.shutdown();

        assertThat(mismatches).isZero();
    }
}
