package com.vryez.backendlab.lab02;

import org.springframework.stereotype.Component;

@Component
public class RateDiscountPolicy implements DiscountPolicy {

    @Override
    public int apply(int price) {
        return price - (price / 10);
    }
}
