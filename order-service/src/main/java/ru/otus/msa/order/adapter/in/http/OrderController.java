package ru.otus.msa.order.adapter.in.http;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.application.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("order-service/api/v1")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("order")
    public OrderDto createOrder(
            @RequestHeader("x-request-id") UUID requestId,
            @RequestBody OrderDto order) {
        return orderService.createOrder(requestId, order);
    }

    @GetMapping("order/{orderId}")
    public OrderDto getOrder(@PathVariable("orderId") UUID orderId) {
        return orderService.getOrderDto(orderId);
    }

    @PatchMapping("order/{orderId}/status")
    public OrderDto changeStatus(@PathVariable("orderId") UUID orderId) {
        return orderService.getOrderDto(orderId);
    }
}
