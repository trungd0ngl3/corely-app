package com.corely.corely_backend.service;

import org.springframework.stereotype.Service;

import com.corely.corely_backend.entity.Product;
import com.corely.corely_backend.entity.ProductVariant;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.repository.ProductRepository;
import com.corely.corely_backend.repository.ProductVariantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class InventoryService {
    ProductRepository productRepository;
    ProductVariantRepository variantRepository;

    public void deductStock(Product product, ProductVariant variant, int quantity) {

        if (variant != null) {

            if (variant.getStockQuantity() < quantity) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }

            variant.setStockQuantity(variant.getStockQuantity() - quantity);

            variantRepository.save(variant);

            return;
        }

        if (product.getStockQuantity() < quantity) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);

        productRepository.save(product);
    }

    public void deduct(java.util.List<com.corely.corely_backend.entity.OrderItem> items) {
        for (var item : items) {
            deductStock(item.getProduct(), item.getVariant(), item.getQuantity());
        }
    }
}
