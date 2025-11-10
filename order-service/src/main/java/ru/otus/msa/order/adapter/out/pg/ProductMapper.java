package ru.otus.msa.order.adapter.out.pg;

import org.mapstruct.Mapper;
import ru.otus.msa.order.adapter.out.pg.repository.entity.ProductEntity;
import ru.otus.msa.order.api.http.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductDto productDto);

    ProductDto toDto(ProductEntity productEntity);
}
