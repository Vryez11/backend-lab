package com.vryez.backendlab.lab22;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GiftPointService {

    private final AccountRepository accountRepository;

    public void gift(long fromId, long toId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("선물 금액은 양수여야 합니다");
        }
        transfer(fromId, toId, amount);
    }

    @Transactional
    public void transfer(long fromId, long toId, int amount) {
        accountRepository.addBalance(fromId, -amount);
        if (accountRepository.isSuspended(toId)) {
            throw new IllegalStateException("정지된 계정에는 선물할 수 없습니다");
        }
        accountRepository.addBalance(toId, amount);
    }
}
