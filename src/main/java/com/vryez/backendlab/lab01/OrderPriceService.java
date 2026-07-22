package com.vryez.backendlab.lab01;

import org.springframework.stereotype.Service;

@Service
public class OrderPriceService {

    private static final int DISCOUNT_PERCENT = 10;

    public OrderResponse order(String userId, long amount) {
        busyWait();
        long finalPrice = amount - (amount * DISCOUNT_PERCENT / 100);
        return new OrderResponse(userId, finalPrice);
    }

    private void busyWait() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
