package ru.otus.msa.order.api.kafka;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;

@Builder
@JsonSerialize
@JsonDeserialize
public record OrderItemEvent(UUID id, UUID productId,
                             Integer quantity,
                             BigDecimal price,
                             BigDecimal depositSum
) {

    @Transient
    public BigDecimal getCoast() {
        return price()
                .multiply(BigDecimal.valueOf(quantity()));
    }

    @Transient
    public BigDecimal getDeposit() {
        return depositSum().multiply(BigDecimal.valueOf(quantity()));
    }
}
