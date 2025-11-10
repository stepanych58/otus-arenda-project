package ru.otus.msa.order.adapter.out.pg;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.adapter.out.pg.entity.OrderEntity;
import ru.otus.msa.order.application.BusinessMetricService;

import static ru.otus.msa.order.api.common.OrderStatus.CREATED;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_RETURN_DEPOSIT;

@Service
@RequiredArgsConstructor
public class OrderMetricsListener {

    private final BusinessMetricService businessMetricService;

    @PostUpdate
    @PostPersist
    public void postPersist(OrderEntity order) {
        if (order.getStatus().equals(CREATED)) {
            //log created metric
            businessMetricService.logOrderCreated();
        } else if (order.getStatus().equals(WAITING_RETURN_DEPOSIT)) {
            //log success order metric
            businessMetricService.logOrderRentSuccess();
        } else if (StringUtils.endsWith(order.getStatus().name(), "_REJECT") ||
                StringUtils.endsWith(order.getStatus().name(), "_ERROR")) {
            //log unsuccess metrics
            businessMetricService.logOrderRentUnSuccess();
        } else {
            //do nothing
        }
    }
}
