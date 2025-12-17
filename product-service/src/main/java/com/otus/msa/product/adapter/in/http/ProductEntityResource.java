package com.otus.msa.product.adapter.in.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.otus.msa.product.adapter.out.kafka.ProductEventStrimmer;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import com.otus.msa.product.domain.ProductService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("product-service/api/v1/product")
@RequiredArgsConstructor
public class ProductEntityResource {

    private final ProductService productEntityService;

    private final ProductEventStrimmer productEventStrimmer;

    @GetMapping
    public PagedModel<ProductEntity> getAll(@ParameterObject @ModelAttribute ProductEntityFilter filter,
                                            @ParameterObject Pageable pageable) {
        Page<ProductEntity> productEntities = productEntityService.getAll(filter, pageable);
        return new PagedModel<>(productEntities);
    }

    @GetMapping("/{id}")
    public ProductEntity getOne(@PathVariable UUID id) {
        return productEntityService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<ProductEntity> getMany(@RequestParam List<UUID> ids) {
        return productEntityService.getMany(ids);
    }

    @PostMapping
    public ProductEntity create(
            @RequestHeader("X-Request-Id") UUID requestId,
            @RequestBody CreateProductDto productEntity, Authentication authentication) {
        productEntity.setUserId(UUID.fromString(String.valueOf(authentication.getPrincipal())));
        return productEntityService.create(requestId, productEntity);
    }

    @PatchMapping("/{id}")
    public ProductEntity patch(@PathVariable UUID id, @RequestBody JsonNode patchNode) throws IOException {
        return productEntityService.patch(id, patchNode);
    }

    @PatchMapping
    public List<UUID> patchMany(@RequestParam List<UUID> ids, @RequestBody JsonNode patchNode) throws IOException {
        return productEntityService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public ProductEntity delete(@PathVariable UUID id) {
        return productEntityService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<UUID> ids) {
        productEntityService.deleteMany(ids);
    }

    @GetMapping("/events")
    public void exportProducts() {
        productEventStrimmer.exportProducts();
    }
}
