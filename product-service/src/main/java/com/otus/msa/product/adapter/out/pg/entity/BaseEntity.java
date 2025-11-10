package com.otus.msa.product.adapter.out.pg.entity;

import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

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
