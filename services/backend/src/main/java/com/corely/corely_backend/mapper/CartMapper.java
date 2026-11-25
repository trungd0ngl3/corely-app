package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.response.cart.CartItemResponse;
import com.corely.corely_backend.entity.Product;
import com.corely.corely_backend.entity.ProductVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CartMapper {

    public CartItemResponse toResponse(Product product, ProductVariant variant, Integer quantity) {
        BigDecimal price = variant != null ? variant.getPrice() : product.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        int stock = variant != null ? variant.getStockQuantity() : product.getStockQuantity();
        String imageUrl = product.getThumbnailUrl();

        return CartItemResponse.builder()
                .productId(product.getId())
                .variantId(variant != null ? variant.getId() : null)
                .productName(product.getName())
                .variantName(variant != null ? variant.getName() : null)
                .imageUrl(imageUrl)
                .price(price)
                .quantity(quantity)
                .subtotal(subtotal)
                .inStock(stock >= quantity)
                .availableStock(stock)
                .storeId(product.getStore().getId())
                .storeName(product.getStore().getName())
                .build();
    }
}
