package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.response.WishlistResponse;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.entity.Wishlist;
import com.corely.corely_backend.entity.WishlistItem;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.WishlistMapper;
import com.corely.corely_backend.repository.ProductRepository;
import com.corely.corely_backend.repository.UserRepository;
import com.corely.corely_backend.repository.WishlistItemRepository;
import com.corely.corely_backend.repository.WishlistRepository;
import com.corely.corely_backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class WishlistService {
    WishlistRepository wishlistRepository;
    WishlistItemRepository wishlistItemRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    WishlistMapper wishlistMapper;
    SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public WishlistResponse getMyWishlist() {
        Wishlist wishlist = wishlistRepository.findByUserId(getCurrentUser().getId())
                .orElseGet(() -> getOrCreateWishlist(getCurrentUser()));
        return wishlistMapper.toWishlistResponse(wishlist);
    }

    @Transactional
    public void addToWishlist(@NonNull UUID productId) {
        var user = getCurrentUser();
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getIsActive())
            throw new AppException(ErrorCode.PRODUCT_NOT_AVAILABLE);

        var wishlist = getOrCreateWishlist(user);

        try {
            wishlistItemRepository.save(WishlistItem.builder().wishlist(wishlist).product(product).build());
        } catch (DataIntegrityViolationException e) {
            log.warn("User {} tried to add duplicated product {}", user.getId(), productId);
            throw new AppException(ErrorCode.PRODUCT_ALREADY_IN_WISHLIST);
        }
    }

    @Transactional
    public void removeFromWishlist(UUID productId) {
        wishlistRepository.findByUserId(getCurrentUser().getId())
                .ifPresent(w -> wishlistItemRepository.deleteByWishlistIdAndProductId(w.getId(), productId));
    }

    private User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }

    private Wishlist getOrCreateWishlist(User user) {
        return wishlistRepository.findByUserId(user.getId())
                .orElseGet(() -> wishlistRepository.save(Wishlist.builder().user(user).build()));
    }
}
