package ru.otus.msa.order.adapter.in.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ru.otus.msa.order.application.ProductService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductEventListener {

    private final ProductService productService;

    @KafkaListener(topics = { "product-event" },
            containerFactory = "commonOrderFactory")
    public void onProductMessage(ProductEvent productEvent) {
        log.info("Получено сообщение от сервиса product productEvent: {}", productEvent);
        productService.createProduct(productEvent);
    }
}
