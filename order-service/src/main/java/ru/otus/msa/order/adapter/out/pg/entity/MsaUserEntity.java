package ru.otus.msa.order.adapter.out.pg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.otus.msa.user.api.kafka.dto.UserEventStatus;

import java.util.UUID;

@Getter
@Setter
@Entity
@ToString(of = {"id", "status"})
@Table(name = "msa_user")
public class MsaUserEntity extends BaseEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private UserEventStatus status;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "pickup_start_time")
    private String pickupStartTime;

    @Column(name = "pickup_end_time")
    private String pickupEndTime;
}
