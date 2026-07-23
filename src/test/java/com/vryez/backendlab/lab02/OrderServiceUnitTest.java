package com.vryez.backendlab.lab02;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderServiceUnitTest {

    private DiscountPolicy discountPolicy;
    private OrderRepository orderRepository;

    @BeforeEach
    void before() {
        discountPolicy = new RateDiscountPolicy();
        orderRepository = new MemoryOrderRepository();
    }

    @Test
    void 할인이_적용된_결제금액을_반환한다() {
        OrderService orderService = new OrderService(discountPolicy, orderRepository);
        int result = orderService.order(1L, 10000);
        assertThat(result).isEqualTo(9000);
    }
}
