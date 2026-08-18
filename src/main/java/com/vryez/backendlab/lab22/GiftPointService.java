package com.vryez.backendlab.lab22;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GiftPointService {

    private final AccountRepository accountRepository;
    private final TransferService transferService;

    public void gift(long fromId, long toId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("선물 금액은 양수여야 합니다");
        }
        transferService.transfer(fromId, toId, amount);
    }
}
