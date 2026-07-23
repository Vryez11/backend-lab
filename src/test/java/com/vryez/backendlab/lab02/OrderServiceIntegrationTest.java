package com.vryez.backendlab.lab02;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OrderServiceIntegrationTest {

    @Autowired
    OrderService orderService;

    @Test
    void 통합_실행에서는_정상() {
        assertThat(orderService.order(1L, 10000)).isEqualTo(9000);
    }
}
