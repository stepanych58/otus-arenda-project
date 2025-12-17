package ru.otus.msa.order.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;
import ru.otus.msa.order.adapter.out.pg.OrderMetricsListener;
import ru.otus.msa.order.api.common.OrderStatus;

import java.util.*;

import static ru.otus.msa.order.api.common.OrderStatus.*;

@Getter
@Setter
@Entity
@EntityListeners({AuditingEntityListener.class, OrderMetricsListener.class})
@Table(name = "msa_order")
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private MsaUserEntity user;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private MsaUserEntity manager;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "name")
    private String name;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "rent_start_date")
    private String rentStartDate;

    @Column(name = "rent_complete_date")
    private String rentCompleteDate;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @Transient
    public void setStatus(OrderStatus newStatus) {
        if (Objects.nonNull(getStatus()) && !isAvailable(newStatus)) {
            throw new RuntimeException(String.format("Невозможно установить статус %s после %s",
                    newStatus,
                    getStatus()));
        }
        this.status = newStatus;
    }

    @Transient
    public boolean isAvailable(OrderStatus newStatus) {
        return availableStatuses.getOrDefault(getStatus(), List.of())
                .contains(newStatus);
    }

    @Transient
    private Map<OrderStatus, List<OrderStatus>> availableStatuses =
            new EnumMap<>(OrderStatus.class) {{

                put(CREATED, List.of(WAITING_RESERVE_PRODUCT));

                put(WAITING_RESERVE_PRODUCT, List.of(RESERVE_PRODUCT_REJECT,
                        RESERVE_PRODUCT_ERROR,
                        WAITING_PAYMENT_DEPOSIT));

                put(WAITING_PAYMENT_DEPOSIT, List.of(PAYMENT_DEPOSIT_REJECT, WAITING_CLIENT_RECEIVE));

                put(PAYMENT_DEPOSIT_REJECT, List.of(WAITING_REVERT_RESERVE_PRODUCT));

                put(WAITING_CLIENT_RECEIVE, List.of(WAITING_PAYMENT_PRODUCT, CLIENT_RECEIVE_REJECT));

                put(WAITING_PAYMENT_PRODUCT, List.of(PAYMENT_PRODUCT_REJECT, WAITING_ADMIN_RECEIVE));

                put(PAYMENT_PRODUCT_REJECT, List.of(WAITING_REVERT_RESERVE_PRODUCT, WAITING_ADMIN_RECEIVE, WAITING_CLIENT_RECEIVE));

                put(WAITING_ADMIN_RECEIVE, List.of(WAITING_RETURN_DEPOSIT, RETURN_DEPOSIT_ERROR));

                put(WAITING_RETURN_DEPOSIT, List.of(WAITING_REVERT_RESERVE_PRODUCT, RETURN_DEPOSIT_ERROR));

                put(WAITING_REVERT_RESERVE_PRODUCT, List.of(REJECTED, COMPLETED, RESERVE_PRODUCT_ERROR));

                put(RESERVE_PRODUCT_REJECT, List.of(REJECTED));
                put(CLIENT_RECEIVE_REJECT, List.of(WAITING_REVERT_RESERVE_PRODUCT));

                put(ADMIN_RECEIVE_REJECT, List.of(REJECTED));
            }};

    public void setRejectReason(String rejectReason) {
        if (StringUtils.hasText(rejectReason)) {
            this.rejectReason = rejectReason;
        }
    }
}
