package ru.otus.msa.user.adapter.out.pg.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import ru.otus.msa.user.adapter.out.pg.repository.entity.User;

import java.time.Instant;

public record UserFilter(String firstNameLike, String lastNameLike, Boolean gender, Instant birthDate,
                         Instant createdAt) {
    public Specification<User> toSpecification() {
        return Specification.where(firstNameLikeSpec())
                .and(lastNameLikeSpec())
                .and(genderSpec())
                .and(birthDateSpec())
                .and(createdAtSpec());
    }

    private Specification<User> firstNameLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(firstNameLike)
                ? cb.like(cb.lower(root.get("firstName")), firstNameLike.toLowerCase())
                : null);
    }

    private Specification<User> lastNameLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(lastNameLike)
                ? cb.like(cb.lower(root.get("lastName")), lastNameLike.toLowerCase())
                : null);
    }

    private Specification<User> genderSpec() {
        return ((root, query, cb) -> gender != null
                ? cb.equal(root.get("gender"), gender)
                : null);
    }

    private Specification<User> birthDateSpec() {
        return ((root, query, cb) -> birthDate != null
                ? cb.equal(root.get("birthDate"), birthDate)
                : null);
    }

    private Specification<User> createdAtSpec() {
        return ((root, query, cb) -> createdAt != null
                ? cb.equal(root.get("createdAt"), createdAt)
                : null);
    }
}