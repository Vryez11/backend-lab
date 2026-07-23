package com.vryez.backendlab.lab02;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final DiscountPolicy discountPolicy;

    private final OrderRepository orderRepository;

    public int order(Long memberId, int price) {
        int payAmount = discountPolicy.apply(price);
        orderRepository.save(memberId, payAmount);
        return payAmount;
    }
}
