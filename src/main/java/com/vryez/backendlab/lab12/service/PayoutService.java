package com.vryez.backendlab.lab12.service;

import com.vryez.backendlab.lab12.domain.Creator;
import com.vryez.backendlab.lab12.domain.Payout;
import com.vryez.backendlab.lab12.exception.PayoutRejectedException;
import com.vryez.backendlab.lab12.repository.CreatorRepository;
import com.vryez.backendlab.lab12.repository.PayoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayoutService {

    private static final long MONTHLY_LIMIT = 500_000L;

    private final CreatorRepository creatorRepository;
    private final PayoutRepository payoutRepository;

    @Transactional
    public void confirmPayout(Long creatorId, long amount) throws PayoutRejectedException {
        Creator creator = creatorRepository.findById(creatorId);

        if (creator.getPayoutBalance() < amount) {
            throw new IllegalArgumentException("정산 가능 잔액 부족");
        }

        // 1) 잔액 차감
        creatorRepository.decreaseBalance(creatorId, amount);

        // 2) 정산 이력 저장
        payoutRepository.save(new Payout(null, creatorId, amount, "PAID"));

        // 3) 월 한도 감사 — 이번 건을 포함한 누적액이 한도를 넘으면 거부
        long thisMonthTotal = payoutRepository.sumThisMonthAmount(creatorId);
        if (thisMonthTotal > MONTHLY_LIMIT) {
            throw new PayoutRejectedException(
                    "월 정산 한도 초과: " + thisMonthTotal + "/" + MONTHLY_LIMIT);
        }
    }
}
