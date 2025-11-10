package com.otus.msa.product.domain;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.databind.JsonNode;
import com.otus.msa.product.adapter.in.http.CreateProductDto;
import com.otus.msa.product.adapter.in.http.ProductEntityFilter;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;

public interface ProductService {
    Page<ProductEntity> getAll(ProductEntityFilter filter, Pageable pageable);

    ProductEntity getOne(UUID id);

    List<ProductEntity> getMany(List<UUID> ids);

    ProductEntity create(UUID requestId, CreateProductDto createProductDto);

    ProductEntity patch(UUID id, JsonNode patchNode) throws IOException;

    List<UUID> patchMany(List<UUID> ids, JsonNode patchNode) throws IOException;

    ProductEntity delete(UUID id);

    void deleteMany(List<UUID> ids);
}
