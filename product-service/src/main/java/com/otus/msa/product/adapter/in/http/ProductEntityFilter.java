package com.otus.msa.product.adapter.in.http;

import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.otus.msa.product.adapter.out.pg.entity.ProductEntity;

public record ProductEntityFilter(String name, Integer quantityLte, Integer quantityGte, String descriptionLike,
                                  BigDecimal depositLte, BigDecimal priceLte, BigDecimal priceGte,
                                  BigDecimal depositGte) {
    public Specification<ProductEntity> toSpecification() {
        return nameSpec()
                .and(quantityLteSpec())
                .and(quantityGteSpec())
                .and(descriptionLikeSpec())
                .and(depositLteSpec())
                .and(priceLteSpec())
                .and(priceGteSpec())
                .and(depositGteSpec());
    }

    private Specification<ProductEntity> nameSpec() {
        return (root, query, cb) -> {
            if (StringUtils.hasText(name)) {
                String pattern = "%" + name.toLowerCase() + "%"; // добавляем % с обеих сторон
                return cb.like(cb.lower(root.get("name")), pattern);
            }
            return null;
        };
    }

    private Specification<ProductEntity> quantityLteSpec() {
        return ((root, query, cb) -> quantityLte != null
                ? cb.lessThanOrEqualTo(root.get("quantity"), quantityLte)
                : null);
    }

    private Specification<ProductEntity> quantityGteSpec() {
        return ((root, query, cb) -> quantityGte != null
                ? cb.greaterThanOrEqualTo(root.get("quantity"), quantityGte)
                : null);
    }

    private Specification<ProductEntity> descriptionLikeSpec() {
        return ((root, query, cb) -> StringUtils.hasText(descriptionLike)
                ? cb.like(cb.lower(root.get("description")), "%" + descriptionLike.toLowerCase() + "%")
                : null);
    }

    private Specification<ProductEntity> depositLteSpec() {
        return ((root, query, cb) -> depositLte != null
                ? cb.lessThanOrEqualTo(root.get("depositSum"), depositLte)
                : null);
    }

    private Specification<ProductEntity> priceLteSpec() {
        return ((root, query, cb) -> priceLte != null
                ? cb.lessThanOrEqualTo(root.get("price"), priceLte)
                : null);
    }

    private Specification<ProductEntity> priceGteSpec() {
        return ((root, query, cb) -> priceGte != null
                ? cb.greaterThanOrEqualTo(root.get("price"), priceGte)
                : null);
    }

    private Specification<ProductEntity> depositGteSpec() {
        return ((root, query, cb) -> depositGte != null
                ? cb.greaterThanOrEqualTo(root.get("depositSum"), depositGte)
                : null);
    }
}