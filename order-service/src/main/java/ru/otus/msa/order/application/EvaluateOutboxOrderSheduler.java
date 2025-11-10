package ru.otus.msa.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.msa.order.adapter.out.pg.repository.OrderHistoryRepository;

import static ru.otus.msa.order.application.SchedulerConfiguration.ORDER_SCHEDULER_BEAN_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluateOutboxOrderSheduler {

    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderOutboxProcessor orderOutboxProcessor;
    @Value("${order-service.created-selector.limit:500}")
    private Integer selectorLimit;

    @Scheduled(fixedRate = 1000)
    @Async(ORDER_SCHEDULER_BEAN_NAME)
    public void processCreatedOrders() {
        PageRequest pageble = PageRequest.of(0, selectorLimit,
                Sort.by("createdAt").descending());
        orderHistoryRepository.findAllBySentFalse(pageble)
                .forEach(oe -> {
                    try {
                        orderOutboxProcessor.process(oe);
                    } catch (Exception e) {
                        log.error("EvaluateCreatedOrdersScheduler error", e);
                    }
                });
    }
}
