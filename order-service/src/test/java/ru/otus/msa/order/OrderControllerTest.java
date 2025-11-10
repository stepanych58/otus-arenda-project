package ru.otus.msa.order;

import com.otus.msa.product.api.kafka.dto.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.otus.msa.order.adapter.out.pg.entity.MsaUserEntity;
import ru.otus.msa.order.adapter.out.pg.entity.OrderHistory;
import ru.otus.msa.order.adapter.out.pg.entity.ProductEntity;
import ru.otus.msa.order.adapter.out.pg.repository.MsaUserRepository;
import ru.otus.msa.order.adapter.out.pg.repository.OrderEntityRepository;
import ru.otus.msa.order.adapter.out.pg.repository.OrderHistoryRepository;
import ru.otus.msa.order.api.http.CurrencyEnumDto;
import ru.otus.msa.order.api.http.OrderDto;
import ru.otus.msa.order.api.http.OrderItemDto;
import ru.otus.msa.order.application.ProductService;
import ru.otus.msa.user.api.kafka.dto.UserEventStatus;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.msa.order.api.common.OrderStatus.WAITING_RESERVE_PRODUCT;

@Slf4j
@AutoConfigureMockMvc
public class OrderControllerTest extends BaseContainerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderEntityRepository orders;
    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private MsaUserRepository users;

    @Autowired
    private ProductService productService;

    @BeforeEach
    void init() {
        orders.deleteAll();
        orderHistoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Тестирование создание заказа. Нет пользователя.")
    public void createOrderTest() throws Exception {
        UUID productId = UUID.randomUUID();
        OrderDto orderDto = OrderDto.builder()
                .userId(UUID.randomUUID())
                .currency(CurrencyEnumDto.RUB)
                .items(List.of(
                        OrderItemDto.builder()
                                .quantity(4)
                                .productId(productId)
                                .build()
                ))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/order-service/api/v1/order")
                        .header("x-request-id", UUID.randomUUID())
                        .content(new ObjectMapper().writeValueAsString(orderDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DisplayName("Тестирование создание заказа. Нет продуктов.")
    public void createOrderTest2() throws Exception {
        MsaUserEntity user = new MsaUserEntity();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setStatus(UserEventStatus.CREATED);

        MsaUserEntity savedUser = users.save(user);
        UUID productId = UUID.randomUUID();
        OrderDto orderDto = OrderDto.builder()
                .userId(userId)
                .currency(CurrencyEnumDto.RUB)
                .items(List.of(
                        OrderItemDto.builder()
                                .quantity(4)
                                .productId(productId)
                                .build()
                ))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/order-service/api/v1/order")
                        .header("x-request-id", UUID.randomUUID())
                        .content(new ObjectMapper().writeValueAsString(orderDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @DisplayName("Тестирование создание заказа.")
    public void createOrderTest3() throws Exception {
        MsaUserEntity user = new MsaUserEntity();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setStatus(UserEventStatus.CREATED);
        user.setPickupAddress("Мориса Тереза 23");
        user.setPickupStartTime("10:00");
        user.setPickupEndTime("20:00");
        MsaUserEntity manager = new MsaUserEntity();
        UUID managerId = UUID.randomUUID();
        manager.setId(managerId);
        manager.setStatus(UserEventStatus.CREATED);
        manager.setPickupAddress("Мориса Тереза 23");
        manager.setPickupStartTime("10:00");
        manager.setPickupEndTime("20:00");
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = productService.createProduct(ProductEvent.builder()
                .productId(productId)
                .rmv(false)
                .price(BigDecimal.valueOf(1000))
                .depositSum(BigDecimal.valueOf(1000))
                .name("test product")
                .description("test product")
                .currency("RUB")
                .quantity(100)
                .userId(managerId)
                .build());

        users.saveAll(List.of(user, manager));

        OrderDto orderDto = OrderDto.builder()
                .userId(userId)
                .rentStartDate("20.11.2025")
                .rentCompleteDate("20.12.2025")
                .currency(CurrencyEnumDto.RUB)
                .items(List.of(
                        OrderItemDto.builder()
                                .quantity(4)
                                .productId(productId)
                                .price(BigDecimal.valueOf(1000))
                                .build()
                ))
                .build();
        mockMvc.perform(MockMvcRequestBuilders.post("/order-service/api/v1/order")
                        .header("x-request-id", UUID.randomUUID())
                        .content(new ObjectMapper().writeValueAsString(orderDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .untilAsserted(() -> {
                            assertThat(orders.findAll().size()).isEqualTo(1);
                        }
                );

        Awaitility.await()
                .atMost(Duration.of(1, ChronoUnit.MINUTES))
                .untilAsserted(() -> {
                            List<OrderHistory> outbox = orderHistoryRepository.findAll();
                            assertThat(outbox.size()).isEqualTo(2);
                            assertTrue(outbox.stream()
                                    .filter(oe -> oe.getStatus().equals(WAITING_RESERVE_PRODUCT))
                                    .anyMatch(OrderHistory::getSent));
                            log.info("{} ", outbox);
                        }
                );
    }
}
