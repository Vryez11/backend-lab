package com.vryez.backendlab.lab01;

import org.springframework.stereotype.Service;

@Service
public class OrderPriceService {

    private static final int DISCOUNT_PERCENT = 10;
    private long lastAmount;

    public synchronized OrderResponse order(String userId, long amount) {
        this.lastAmount = amount;
        busyWait();
        long finalPrice = this.lastAmount - (this.lastAmount * DISCOUNT_PERCENT / 100);
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
