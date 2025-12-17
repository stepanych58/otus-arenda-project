package ru.otus.msa.user.adapter.out.pg.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pickup_point")
public class PickupPoint extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "address")
    private String oneLineAddress;
    @Column(name = "start_time")
    private String startTime;
    @Column(name = "end_time")
    private String endTime;
}
