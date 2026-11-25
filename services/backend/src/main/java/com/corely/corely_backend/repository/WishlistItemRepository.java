package com.corely.corely_backend.repository;

import com.corely.corely_backend.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
    void deleteByWishlistIdAndProductId(UUID wishlistId, UUID productId);
    boolean existsByWishlistIdAndProductId(UUID wishlistId, UUID productId);
    Optional<WishlistItem> findByWishlistIdAndProductId(UUID wishlistId, UUID productId);
}
