package com.otus.msa.product.adapter.in.http;

import java.math.BigDecimal;
import java.util.UUID;

import com.otus.msa.product.adapter.out.pg.entity.CurrencyEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateProductDto {
    private UUID userId;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal depositSum;

    private Integer quantity;

    private String imgUrl;

    private CurrencyEnum currency;
}
