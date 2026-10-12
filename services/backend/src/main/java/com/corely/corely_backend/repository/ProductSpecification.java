package com.corely.corely_backend.repository;

import com.corely.corely_backend.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.UUID;

public class ProductSpecification {
    public static Specification<Product> filter(String q, UUID categoryId, UUID brandId, UUID storeId, Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            predicates.add(cb.isTrue(root.get("isActive")));
            if (org.springframework.util.StringUtils.hasText(q)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + q.trim().toLowerCase() + "%"));
            }
            if (categoryId != null) predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            if (brandId != null) predicates.add(cb.equal(root.get("brand").get("id"), brandId));
            if (storeId != null) predicates.add(cb.equal(root.get("store").get("id"), storeId));
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
