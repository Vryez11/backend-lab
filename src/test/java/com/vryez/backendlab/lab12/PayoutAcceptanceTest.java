package com.vryez.backendlab.lab12;

import com.vryez.backendlab.lab12.exception.PayoutRejectedException;
import com.vryez.backendlab.lab12.repository.CreatorRepository;
import com.vryez.backendlab.lab12.repository.PayoutRepository;
import com.vryez.backendlab.lab12.service.PayoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PayoutAcceptanceTest {

    @Autowired
    PayoutService payoutService;

    @Autowired
    CreatorRepository creatorRepository;

    @Autowired
    PayoutRepository payoutRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("delete from payout");
        jdbcTemplate.update(
                "merge into creator (id, name, payout_balance) key (id) values (1, '우지', 1000000)");
    }

    private long balanceOf(long creatorId) {
        return creatorRepository.findById(creatorId).getPayoutBalance();
    }

    @Test
    void T1_정상_정산은_잔액차감과_이력저장이_모두_반영된다() throws Exception {
        payoutService.confirmPayout(1L, 300_000);

        assertThat(balanceOf(1L)).isEqualTo(700_000L);
        assertThat(payoutRepository.countByCreator(1L)).isEqualTo(1);
    }

    @Test
    void T2_한도초과_정산은_거부되고_아무것도_바뀌지_않는다() throws Exception {
        payoutService.confirmPayout(1L, 300_000);   // 누적 300,000 — 한도 안쪽, 정상 처리

        assertThatThrownBy(() -> payoutService.confirmPayout(1L, 300_000))
                .isInstanceOf(PayoutRejectedException.class)
                .hasMessageContaining("한도 초과");

        // 거부됐다면 두 번째 신청의 흔적이 아무것도 남아 있으면 안 된다
        assertThat(balanceOf(1L)).isEqualTo(700_000L);
        assertThat(payoutRepository.countByCreator(1L)).isEqualTo(1);
    }

    @Test
    void T3_잔액부족은_예외가_나고_아무것도_바뀌지_않는다() {
        assertThatThrownBy(() -> payoutService.confirmPayout(1L, 2_000_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액 부족");

        assertThat(balanceOf(1L)).isEqualTo(1_000_000L);
        assertThat(payoutRepository.countByCreator(1L)).isEqualTo(0);
    }
}
