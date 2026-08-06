package com.vryez.backendlab.lab13;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 운영 신고 증상: 동시 후원이 몰릴 때만 영수증에 남의 닉네임·금액이 찍힌다.
 * 단건 테스트로는 절대 재현되지 않는다.
 *
 * 아래 배선은 N개의 서로 다른 후원을 동시에 쏘고,
 * i번째 요청의 응답 영수증을 receipts[i]에 담아준다.
 * "무엇이 오염인가"를 단언으로 정의하는 것은 이 과제의 몫이다 — TODO를 채워라.
 */
@SpringBootTest
class GiftReceiptConcurrencyTest {

    @Autowired GiftReceiptService giftReceiptService;
    @Autowired GiftRepository giftRepository;

    @BeforeEach
    void clean() {
        giftRepository.deleteAll();
    }

    @Test
    void 동시_후원이_몰려도_영수증이_뒤섞이면_안_된다() throws Exception {
        int threads = 50;
        int rounds = 10; // 오염은 확률적 현상이다 — 라운드를 반복해 재현 확률을 끌어올린다

        for (int round = 0; round < rounds; round++) {
            String[] names = new String[threads];
            long[] amounts = new long[threads];
            for (int i = 0; i < threads; i++) {
                names[i] = "viewer-" + i;
                amounts[i] = (i + 1) * 1_000L;
            }

            GiftReceipt[] receipts = new GiftReceipt[threads];
            runConcurrently(threads, i -> receipts[i] = giftReceiptService.issue(names[i], amounts[i]));

            for (int i = 0; i < threads; i++) {

                String name = receipts[i].viewerName();
                long amount = receipts[i].amount();
                String grade = receipts[i].grade();

                assertThat(amount).isEqualTo((i + 1) * 1_000L);
                assertThat(grade).isEqualTo(gradeToAmount(amount));
            }
        }
    }

    /**
     * threads개의 스레드가 동시에 출발해 각자 task.accept(자기 인덱스)를 실행하고,
     * 전부 끝날 때까지 기다린다.
     */
    private void runConcurrently(int threads, IntConsumer task) throws InterruptedException {
        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            CountDownLatch ready = new CountDownLatch(threads);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(threads);

            for (int t = 0; t < threads; t++) {
                int index = t;
                pool.submit(() -> {
                    ready.countDown();
                    try {
                        start.await();
                        task.accept(index);
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
                throw new AssertionError("동시 부하가 30초 안에 끝나지 않았다 — 데드락 또는 작업 중 예외를 의심하라");
            }
        }
    }

    private String gradeToAmount(long amount) {

        if (amount >= 100_000) return "다이아";
        if (amount >= 10_000) return "골드";
        return "브론즈";
    }
}
