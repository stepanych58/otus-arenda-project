package com.otus.msa.product.adapter.out.pg;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otus.msa.product.adapter.in.http.CreateProductDto;
import com.otus.msa.product.adapter.in.http.ProductEntityFilter;
import com.otus.msa.product.adapter.out.kafka.ProductEventMapper;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import com.otus.msa.product.adapter.out.pg.entity.ProductRequest;
import com.otus.msa.product.adapter.out.pg.repository.ProductEntityRepository;
import com.otus.msa.product.adapter.out.pg.repository.ProductRequestRepository;
import com.otus.msa.product.api.kafka.dto.ProductEvent;
import com.otus.msa.product.domain.ProductEventSender;
import com.otus.msa.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductEntityServiceImpl implements ProductService {

    private final ProductEntityRepository productEntityRepository;

    private final ObjectMapper objectMapper;

    private final ProductMapper productMapper;

    private final ProductEventSender productEventSender;

    private final ProductEventMapper productEventMapper;
    private final ProductRequestRepository productRequestRepository;

//todo сделать отправку product event с помощью аннотаций

    @Override
    public Page<ProductEntity> getAll(ProductEntityFilter filter, Pageable pageable) {
        Specification<ProductEntity> spec = filter.toSpecification();
        return productEntityRepository.findAll(spec, pageable);
    }

    @Override
    public ProductEntity getOne(UUID id) {
        Optional<ProductEntity> productEntityOptional = productEntityRepository.findById(id);
        return productEntityOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entity with id `%s` not found".formatted(id)));
    }

    @Override
    public List<ProductEntity> getMany(List<UUID> ids) {
        return productEntityRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public ProductEntity create(UUID requestId, CreateProductDto createProductDto) {
        Optional<ProductRequest> byRequestId = productRequestRepository.findByRequestId(requestId);
        if (byRequestId.isPresent()) {
            return getOne(byRequestId.get().getProductId());
        } else {
            ProductEntity productEntity = productMapper.toEntity(createProductDto);
            ProductEntity savedProduct = productEntityRepository.save(productEntity);
            productRequestRepository.save(new ProductRequest(savedProduct.getId(), requestId));
            //todo вынести отправку в jpa лиснер
            productEventSender.send(productEventMapper.fromEntity(savedProduct));
            return savedProduct;

        }
    }

    @Override
    public ProductEntity patch(UUID id, JsonNode patchNode) throws IOException {
        ProductEntity productEntity = productEntityRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Entity with id `%s` not found".formatted(
                                id)));

        objectMapper.readerForUpdating(productEntity).readValue(patchNode);

        ProductEntity savedProduct = productEntityRepository.save(productEntity);
        productEventSender.send(productEventMapper.fromEntity(savedProduct));
        return savedProduct;
    }

    @Override
    public List<UUID> patchMany(List<UUID> ids, JsonNode patchNode) throws IOException {
        Collection<ProductEntity> productEntities = productEntityRepository.findAllById(ids);

        for (ProductEntity productEntity : productEntities) {
            objectMapper.readerForUpdating(productEntity).readValue(patchNode);
        }

        List<ProductEntity> resultProductEntities = productEntityRepository.saveAll(productEntities);
        resultProductEntities.forEach(savedProduct -> {
            productEventSender.send(productEventMapper.fromEntity(savedProduct));
        });
        return resultProductEntities.stream()
                .map(ProductEntity::getId)
                .toList();
    }

    @Override
    public ProductEntity delete(UUID id) {
        ProductEntity productEntity = productEntityRepository.findById(id).orElse(null);
        if (productEntity != null) {
            productEntityRepository.delete(productEntity);
            productEventSender.send(ProductEvent.builder()
                    .eventId(UUID.randomUUID())
                    .productId(productEntity.getId())
                    .rmv(true)
                    .build());
        }
        return productEntity;
    }

    @Override
    public void deleteMany(List<UUID> ids) {
        productEntityRepository.deleteAllById(ids);
        ids.forEach(productId -> productEventSender.send(ProductEvent.builder()
                .eventId(UUID.randomUUID())
                .productId(productId)
                .rmv(true)
                .build()));
    }
}
