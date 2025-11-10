package ru.otus.msa.user.adapter.out.pg.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_profile")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String firstName;

    @Column(name = "last_name")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private String lastName;

    @Column(name = "gender", nullable = false)
    @JdbcTypeCode(SqlTypes.BOOLEAN)
    private Boolean gender;

    @Column(name = "birth_date")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private Instant birthDate;

    @Column(name = "created_at")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private Instant createdAt;

    @Column(name = "modified_at")
    @JdbcTypeCode(SqlTypes.TIMESTAMP)
    private Instant modifiedAt;
}