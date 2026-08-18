package com.vryez.backendlab.lab22;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {

    public final AccountRepository accountRepository;

    @Transactional
    public void transfer(long fromId, long toId, int amount) {
        accountRepository.addBalance(fromId, -amount);
        if (accountRepository.isSuspended(toId)) {
            throw new IllegalStateException("정지된 계정에는 선물할 수 없습니다");
        }
        accountRepository.addBalance(toId, amount);
    }
}
