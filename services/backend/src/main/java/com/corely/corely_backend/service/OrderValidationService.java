package com.corely.corely_backend.service;

import org.springframework.stereotype.Service;

import com.corely.corely_backend.dto.response.cart.CartResponse;
import com.corely.corely_backend.entity.Product;
import com.corely.corely_backend.entity.ProductVariant;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderValidationService {

    public void validateCart(CartResponse cart) {

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }
    }

    public void validateProduct(Product product) {

        if (!product.getIsActive()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    public void validateVariant(ProductVariant variant) {

        if (variant != null && !variant.getIsActive()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_AVAILABLE);
        }
    }

    public void validateStock(int available, int requested) {

        if (available < requested) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }
    }
}