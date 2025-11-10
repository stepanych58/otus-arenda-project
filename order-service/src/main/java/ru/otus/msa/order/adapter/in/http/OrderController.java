package ru.otus.msa.order.adapter.in.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.application.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("order-service/api/v1")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("order")
    public OrderDto createOrder(@RequestBody OrderDto order) {
        return orderService.createOrder(order);
    }

    @GetMapping("order/{orderId}")
    public OrderDto getOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.getOrder(orderId);
    }
}
