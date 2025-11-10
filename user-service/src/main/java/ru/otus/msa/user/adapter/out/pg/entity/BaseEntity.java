package ru.otus.msa.user.adapter.out.pg.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    protected Instant createdAt;

    @LastModifiedDate
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    protected Instant modifiedAt;
}
