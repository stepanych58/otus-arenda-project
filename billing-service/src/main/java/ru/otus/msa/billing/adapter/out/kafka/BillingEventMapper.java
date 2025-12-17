package ru.otus.msa.billing.adapter.out.kafka;

import org.mapstruct.Mapper;
import ru.otus.msa.billing.adapter.out.pg.entity.BillingOrderOutboxEntity;
import ru.otus.msa.user.api.kafka.dto.BillingEvent;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface BillingEventMapper {

    BillingEvent toEvent(BillingOrderOutboxEntity outbox);
}
