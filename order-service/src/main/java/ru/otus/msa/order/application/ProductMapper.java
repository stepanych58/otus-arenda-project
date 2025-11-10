package ru.otus.msa.order.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import ru.otus.msa.order.adapter.out.pg.entity.ProductEntity;
import ru.otus.msa.order.api.http.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(ProductDto productDto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    ProductEntity toEntity(ProductEvent productEvent);

    ProductDto toDto(ProductEntity productEntity);
}
