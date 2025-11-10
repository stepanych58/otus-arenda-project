package ru.otus.msa.notification.adapter.out.pg.entity;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(of = { "id", "userId", "orderId", "content" })
@Entity
@Table(name = "notification")
public class NotificationEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private UUID orderId;

    private String content;

    @Column(name = "created_at")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private Instant createdAt;

    @Column(name = "modified_at")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private Instant modifiedAt;
}
