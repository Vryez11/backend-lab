package com.vryez.backendlab.lab22;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class GiftPointServiceTest {

    @Autowired
    private GiftPointService giftPointService;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void T1_gift로_선물을_보내면_정지상태일때_둘다_금액이_그대로여야_한다(){

        long fromId = 1L;
        long suspendToId = 2L;

        Account fromAccount = accountRepository.findById(fromId);
        Account toAccount = accountRepository.findById(suspendToId);
        int beforeFromBalance = fromAccount.balance();
        int beforeToBalance = toAccount.balance();
        log.info("이전 잔액={}", beforeFromBalance);
        assertThrows(IllegalStateException.class, () -> giftPointService.gift(fromId, suspendToId, 500));
        fromAccount = accountRepository.findById(fromId);
        int afterBalance = fromAccount.balance();
        int afterToBalance = toAccount.balance();
        log.info("이후 잔액={}", afterBalance);

        assertThat(beforeFromBalance).isEqualTo(afterBalance);
        assertThat(beforeToBalance).isEqualTo(afterToBalance);
    }
}