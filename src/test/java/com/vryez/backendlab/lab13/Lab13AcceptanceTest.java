package com.vryez.backendlab.lab13;

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
 * lab13 채점용 인수 테스트 — 완료 조건 4개를 검증한다.
 * ① 교차 오염 0건(닉네임·금액·등급 세 필드 모두 입력과 일치)
 * ② 단건 동작·등급 경계 유지  ③ 정산 정합성(DB 합계 = 응답 합계)
 */
@SpringBootTest
class Lab13AcceptanceTest {

    @Autowired GiftReceiptService giftReceiptService;
    @Autowired GiftRepository giftRepository;

    @BeforeEach
    void clean() {
        giftRepository.deleteAll();
    }

    @Test
    void 교차오염_0건_닉네임_금액_등급이_모두_자기_입력과_일치한다() throws Exception {
        int threads = 60;
        int rounds = 10;

        for (int round = 0; round < rounds; round++) {
            String[] names = new String[threads];
            long[] amounts = new long[threads];
            for (int i = 0; i < threads; i++) {
                names[i] = "r" + round + "-viewer-" + i;
                amounts[i] = (i + 1) * 3_000L; // 3천 ~ 18만: 브론즈·골드·다이아 전 구간
            }

            GiftReceipt[] receipts = new GiftReceipt[threads];
            runConcurrently(threads, i -> receipts[i] = giftReceiptService.issue(names[i], amounts[i]));

            for (int i = 0; i < threads; i++) {
                assertThat(receipts[i].viewerName())
                        .as("round %d, 요청 %d의 영수증에 남의 닉네임이 찍혔다", round, i)
                        .isEqualTo(names[i]);
                assertThat(receipts[i].amount())
                        .as("round %d, 요청 %d의 영수증에 남의 금액이 찍혔다", round, i)
                        .isEqualTo(amounts[i]);
                assertThat(receipts[i].grade())
                        .as("round %d, 요청 %d의 등급이 입력 금액과 어긋난다", round, i)
                        .isEqualTo(expectedGrade(amounts[i]));
            }
        }
    }

    @Test
    void 정산_정합성_DB_적재_합계와_응답_금액_합계가_일치한다() throws Exception {
        int threads = 50;
        long[] amounts = new long[threads];
        for (int i = 0; i < threads; i++) {
            amounts[i] = (i + 1) * 1_000L;
        }

        GiftReceipt[] receipts = new GiftReceipt[threads];
        runConcurrently(threads, i -> receipts[i] = giftReceiptService.issue("viewer-" + i, amounts[i]));

        long inputSum = 0, responseSum = 0;
        for (int i = 0; i < threads; i++) {
            inputSum += amounts[i];
            responseSum += receipts[i].amount();
        }
        assertThat(responseSum).as("응답 금액 합계가 입력 합계와 다르다").isEqualTo(inputSum);
        assertThat(giftRepository.sumAmount()).as("DB 적재 합계가 응답 합계와 다르다").isEqualTo(responseSum);
    }

    @Test
    void 단건_동작과_등급_경계가_유지된다() {
        assertThat(giftReceiptService.issue("혼자온시청자", 9_999L))
                .isEqualTo(new GiftReceipt("혼자온시청자", 9_999L, "브론즈"));
        assertThat(giftReceiptService.issue("혼자온시청자", 10_000L))
                .isEqualTo(new GiftReceipt("혼자온시청자", 10_000L, "골드"));
        assertThat(giftReceiptService.issue("혼자온시청자", 99_999L))
                .isEqualTo(new GiftReceipt("혼자온시청자", 99_999L, "골드"));
        assertThat(giftReceiptService.issue("혼자온시청자", 100_000L))
                .isEqualTo(new GiftReceipt("혼자온시청자", 100_000L, "다이아"));
    }

    private String expectedGrade(long amount) {
        if (amount >= 100_000) return "다이아";
        if (amount >= 10_000) return "골드";
        return "브론즈";
    }

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
                throw new AssertionError("동시 부하가 30초 안에 끝나지 않았다");
            }
        }
    }
}
