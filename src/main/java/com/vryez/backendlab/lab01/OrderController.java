package com.vryez.backendlab.lab01;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lab01")
public class OrderController {

    private final OrderPriceService orderPriceService;

    public OrderController(OrderPriceService orderPriceService) {
        this.orderPriceService = orderPriceService;
    }

    @PostMapping("/orders")
    public OrderResponse order(@RequestBody OrderRequest request) {
        return orderPriceService.order(request.userId(), request.amount());
    }
}
