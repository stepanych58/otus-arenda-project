package com.otus.msa.product.adapter.out.pg;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.otus.msa.product.adapter.out.kafka.ProductEventMapper;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import com.otus.msa.product.adapter.out.pg.entity.ProductReservationEntity;
import com.otus.msa.product.adapter.out.pg.repository.ProductReservationEntityRepository;
import com.otus.msa.product.application.exception.ReservationException;
import com.otus.msa.product.domain.ProductEventSender;
import com.otus.msa.product.domain.ProductReservationService;
import com.otus.msa.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import ru.otus.msa.order.api.kafka.OrderEvent;

@Service
@RequiredArgsConstructor
public class ProductReservationServiceImpl implements ProductReservationService {

    private final ProductReservationEntityRepository reservationRepository;

    private final ProductService productService;

    //todo сделать дженеричную логику по отправке product event
    private final ProductEventSender productEventSender;

    private final ProductEventMapper productEventMapper;

    private final ProductReservationEntityRepository productReservationEntityRepository;

    @Transactional
    @Override
    public void createReservation(OrderEvent orderEvent) {
        List<UUID> productIds = orderEvent.getProductIds();
        Map<UUID, ProductEntity> productsMap = productService.getMany(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, v -> v));
        List<ProductReservationEntity> reserveProductsList = orderEvent.items()
                .stream()
                .map(oi -> {
                    ProductReservationEntity productReservationEntity = new ProductReservationEntity();
                    UUID productId = oi.productId();
                    productReservationEntity.setProductId(productId);
                    productReservationEntity.setUserId(orderEvent.userId());
                    ProductEntity productEntity = productsMap.get(productId);
                    Integer productQuantity = productEntity.getQuantity();
                    Integer itemQuantity = oi.quantity();
                    if (itemQuantity > productQuantity) {
                        throw new ReservationException("Недостаточное количество продуктов у продавца");
                    }
                    productReservationEntity.setCount(itemQuantity);
                    productEntity.setQuantity(productQuantity - itemQuantity);
                    productReservationEntity.setOrderId(orderEvent.orderId());
                    productEventSender.send(productEventMapper.fromEntity(productEntity));
                    return productReservationEntity;
                })
                .toList();
        reservationRepository.saveAll(reserveProductsList);
    }

    @Override
    @Transactional
    public void cancelReservation(OrderEvent orderEvent) {
        List<UUID> productIds = orderEvent.getProductIds();
        Map<UUID, ProductEntity> productsMap = productService.getMany(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, v -> v));
        List<ProductReservationEntity> reservations = reservationRepository.findByOrderIdAndUserId(orderEvent.orderId(),
                                                                                                   orderEvent.userId());
        Map<UUID, ProductReservationEntity> reservationEntityMap = reservations.stream()
                .collect(Collectors.toMap(ProductReservationEntity::getProductId, v -> v));
        productsMap.forEach((productId, productEntity) -> {
            ProductReservationEntity productReservationEntity = reservationEntityMap.get(productId);
            productEntity.setQuantity(productEntity.getQuantity() + productReservationEntity.getCount());
            productEventSender.send(productEventMapper.fromEntity(productEntity));
        });
        reservationRepository.deleteAllByOrderIdAndUserId(orderEvent.orderId(), orderEvent.userId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createByProduct(ProductEntity productEntity) {
        ProductReservationEntity productReservationEntity = new ProductReservationEntity();
        productReservationEntity.setCount(10);
        productReservationEntity.setProductId(productEntity.getId());
        productReservationEntity.setUserId(UUID.randomUUID());
        productReservationEntity.setOrderId(UUID.randomUUID());
        productReservationEntityRepository.save(productReservationEntity);
    }
}
