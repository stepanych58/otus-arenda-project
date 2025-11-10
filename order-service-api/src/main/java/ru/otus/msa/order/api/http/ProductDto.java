package ru.otus.msa.order.api.http;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(UUID id, String name, BigDecimal price, CurrencyEnumDto currency) {
}
