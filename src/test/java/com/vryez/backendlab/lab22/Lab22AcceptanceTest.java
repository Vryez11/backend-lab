package com.vryez.backendlab.lab22;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class Lab22AcceptanceTest {

    @Autowired
    GiftPointService giftPointService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void resetSeed() {
        jdbcTemplate.update("merge into account (id, name, balance, suspended) values (1, '시청자보내는사람', 1000, false)");
        jdbcTemplate.update("merge into account (id, name, balance, suspended) values (2, '정지크리에이터', 0, true)");
        jdbcTemplate.update("merge into account (id, name, balance, suspended) values (3, '정상크리에이터', 0, false)");
    }

    @Test
    @DisplayName("정지 계정 선물 실패 시 양쪽 잔액 모두 전액 원복")
    void 실패시_전액_롤백() {
        assertThatThrownBy(() -> giftPointService.gift(1L, 2L, 500))
                .isInstanceOf(IllegalStateException.class);

        assertThat(accountRepository.findById(1L).balance()).isEqualTo(1000);
        assertThat(accountRepository.findById(2L).balance()).isEqualTo(0);
    }

    @Test
    @DisplayName("정상 계정 선물 성공 시 보낸 쪽 감소, 받는 쪽 증가")
    void 성공시_정상이체() {
        giftPointService.gift(1L, 3L, 300);

        assertThat(accountRepository.findById(1L).balance()).isEqualTo(700);
        assertThat(accountRepository.findById(3L).balance()).isEqualTo(300);
    }
}
