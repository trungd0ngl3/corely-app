package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.WishlistResponse;
import com.corely.corely_backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class WishlistController {
    WishlistService wishlistService;

    @GetMapping
    public ApiResponse<WishlistResponse> getMyWishlist() {
        return ApiResponse.<WishlistResponse>builder()
                .result(wishlistService.getMyWishlist())
                .build();
    }

    @PostMapping("/{productId}")
    public ApiResponse<Void> addToWishlist(@PathVariable UUID productId) {
        wishlistService.addToWishlist(productId);
        return ApiResponse.<Void>builder().build();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<Void> removeFromWishlist(@PathVariable UUID productId) {
        wishlistService.removeFromWishlist(productId);
        return ApiResponse.<Void>builder().build();
    }
}
