package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.store.ReviewRequest;
import com.corely.corely_backend.dto.response.ReviewResponse;
import com.corely.corely_backend.entity.Review;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.ReviewMapper;
import com.corely.corely_backend.repository.ProductRepository;
import com.corely.corely_backend.repository.ReviewRepository;
import com.corely.corely_backend.repository.UserRepository;
import com.corely.corely_backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    ReviewMapper reviewMapper;
    SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable)
                .map(reviewMapper::toReviewResponse);
    }

    @Transactional
    public ReviewResponse createReview(UUID productId, ReviewRequest request) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        var user = getCurrentUser();

        if (reviewRepository.existsByUserIdAndProductId(user.getId(), productId))
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);

        Review review = reviewMapper.toReview(request);
        review.setProduct(product);
        review.setUser(user);

        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponse updateReview(UUID id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        if (!review.getUser().getId().equals(getCurrentUser().getId()))
            throw new AppException(ErrorCode.FORBIDDEN);

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        return reviewMapper.toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        var user = getCurrentUser();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"));

        if (!review.getUser().getId().equals(user.getId()) && !isAdmin)
            throw new AppException(ErrorCode.FORBIDDEN);

        reviewRepository.delete(review);
    }

    private User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }
}
