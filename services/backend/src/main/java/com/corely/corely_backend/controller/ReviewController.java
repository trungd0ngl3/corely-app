package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.store.ReviewRequest;
import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.ReviewResponse;
import com.corely.corely_backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    public ApiResponse<Page<ReviewResponse>> getProductReviews(@PathVariable UUID productId, Pageable pageable) {
        return ApiResponse.<Page<ReviewResponse>>builder()
                .result(reviewService.getProductReviews(productId, pageable))
                .build();
    }

    @PostMapping("/products/{productId}/reviews")
    public ApiResponse<ReviewResponse> createReview(@PathVariable UUID productId, @RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.createReview(productId, request))
                .build();
    }

    @PutMapping("/reviews/{id}")
    public ApiResponse<ReviewResponse> updateReview(@PathVariable UUID id, @RequestBody @Valid ReviewRequest request) {
        return ApiResponse.<ReviewResponse>builder()
                .result(reviewService.updateReview(id, request))
                .build();
    }

    @DeleteMapping("/reviews/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ApiResponse.<Void>builder().build();
    }
}
