package ru.otus.msa.order.adapter.in.kafka;

import com.otus.msa.product.api.kafka.dto.ProductOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.application.OrderService;

@Service
@Log4j2
@RequiredArgsConstructor
public class ProductOrderEventListener {
    private final OrderService orderService;

    @KafkaListener(topics = {"product-order-event"},
            containerFactory = "commonOrderFactory")
    public void onProductOrderMessage(ProductOrderEvent productEvent) {
        log.info("Получено сообщение от сервиса product: {}",productEvent);
        orderService.updateByProductEvent(productEvent);
    }
}
