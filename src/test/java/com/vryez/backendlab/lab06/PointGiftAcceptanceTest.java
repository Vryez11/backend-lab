package com.vryez.backendlab.lab06;

import com.vryez.backendlab.lab06.domain.UserPoint;
import com.vryez.backendlab.lab06.exception.GiftRejectedException;
import com.vryez.backendlab.lab06.repository.UserPointRepository;
import com.vryez.backendlab.lab06.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PointGiftAcceptanceTest {

    @Autowired
    PointService pointService;

    @Autowired
    UserPointRepository repository;

    @BeforeEach
    void seed() {
        repository.save(new UserPoint("viewer1", 1000, true));
        repository.save(new UserPoint("viewer2", 100, true));
        repository.save(new UserPoint("creator1", 500, true));
        repository.save(new UserPoint("creator2", 500, false));
    }

    private long pointOf(String userId) {
        return repository.findById(userId).orElseThrow().getPoint();
    }

    @Test
    void T1_정상_후원_차감과_증가가_정확하다() throws Exception {
        pointService.gift("viewer1", "creator1", 300);

        assertThat(pointOf("viewer1")).isEqualTo(700L);
        assertThat(pointOf("creator1")).isEqualTo(800L);
    }

    @Test
    void T2_수신_비활성_크리에이터_후원은_거절되고_포인트가_원상복구된다() {
        assertThatThrownBy(() -> pointService.gift("viewer1", "creator2", 300))
                .isInstanceOf(GiftRejectedException.class)
                .hasMessageContaining("후원을 받지 않습니다");

        assertThat(pointOf("viewer1")).isEqualTo(1000L);
        assertThat(pointOf("creator2")).isEqualTo(500L);
    }

    @Test
    void T3_잔액_부족이면_거절되고_포인트가_유지된다() {
        assertThatThrownBy(() -> pointService.gift("viewer2", "creator1", 300))
                .isInstanceOf(GiftRejectedException.class)
                .hasMessageContaining("포인트가 부족합니다");

        assertThat(pointOf("viewer2")).isEqualTo(100L);
        assertThat(pointOf("creator1")).isEqualTo(500L);
    }
}
