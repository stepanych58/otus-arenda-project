package com.otus.msa.product.adapter.out.pg;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import org.mapstruct.Mapper;

import com.otus.msa.product.adapter.in.http.CreateProductDto;
import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;

@Mapper(componentModel = SPRING)
public interface ProductMapper {
    ProductEntity toEntity(CreateProductDto productDto);
}
