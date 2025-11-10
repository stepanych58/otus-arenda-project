package com.otus.msa.product.adapter.out.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.otus.msa.product.adapter.out.pg.repository.ProductEntityRepository;
import com.otus.msa.product.api.kafka.dto.ProductEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductEventStrimmer {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    private final ProductEntityRepository productRepository;

    private final ProductEventMapper eventMapper;

    public void exportProducts() {
        productRepository.findAll()
                .forEach(product ->
                                 kafkaTemplate.send(
                                         kafkaTemplate.getDefaultTopic(),
                                         product.getId().toString(),//key
                                         eventMapper.fromEntity(product)));
    }
}
