package com.otus.msa.product.adapter.out.kafka;

import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;
import com.otus.msa.product.api.kafka.dto.ProductEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ProductEventMapper {

    @Mappings({
            @Mapping(target = "eventId", source = "source", qualifiedByName = "randomUUID"),
            @Mapping(target = "productId", source = "id")

    })
    ProductEvent fromEntity(ProductEntity source);

    @Named("randomUUID")
    default UUID randomUUID(ProductEntity source) {
        return UUID.randomUUID();
    }
}
