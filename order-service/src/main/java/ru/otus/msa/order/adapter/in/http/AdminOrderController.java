package ru.otus.msa.order.adapter.in.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.application.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("order-service/admin-api/v1/")
@Slf4j
public class AdminOrderController {

    private final OrderService orderService;

    //todo сделать отдельные статусы а не все
    @PostMapping("/clientReceive/{orderId}")
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestBody ClientReceiveDto clientReceiveDto) {
        //todo исправить чтобы можно было много раз вызвать
        OrderDto orderDto = orderService.clientReceive(orderId, clientReceiveDto);
        return ResponseEntity.ok(orderDto);
    }


    @PostMapping("/adminReceive/{orderId}")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable("orderId") UUID orderId,
            @RequestBody AdminReceiveDto adminReceiveDto) {
        //todo исправить чтобы можно было много раз вызвать
        OrderDto orderDto = orderService.adminReceive(orderId, adminReceiveDto);
        return ResponseEntity.ok(orderDto);
    }

}
