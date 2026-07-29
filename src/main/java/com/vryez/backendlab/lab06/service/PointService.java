package com.vryez.backendlab.lab06.service;

import com.vryez.backendlab.lab06.domain.UserPoint;
import com.vryez.backendlab.lab06.exception.GiftRejectedException;
import com.vryez.backendlab.lab06.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository repository;

    @Transactional(rollbackForClassName = {"GiftRejectedException"})
    public void gift(String fromUserId, String toUserId, long amount) throws GiftRejectedException {
        UserPoint from = repository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("시청자 없음"));
        UserPoint to = repository.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("크리에이터 없음"));

        if (from.getPoint() < amount) {
            throw new GiftRejectedException("포인트가 부족합니다");
        }

        // 1) 시청자 차감
        repository.updatePoint(fromUserId, from.getPoint() - amount);

        // 2) 크리에이터가 후원을 받을 수 있는 상태인지 확인
        if (!to.isGiftEnabled()) {
            throw new GiftRejectedException("이 크리에이터는 현재 후원을 받지 않습니다");
        }

        // 3) 크리에이터 증가
        repository.updatePoint(toUserId, to.getPoint() + amount);
    }
}
