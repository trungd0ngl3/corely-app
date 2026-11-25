package com.corely.corely_backend.factory;

import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.entity.OrderItem;
import com.corely.corely_backend.entity.Product;
import com.corely.corely_backend.entity.ProductVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemFactory {

    public OrderItem create(Product product, ProductVariant variant, Integer quantity, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setVariant(variant);
        orderItem.setQuantity(quantity);
        
        BigDecimal price = (variant != null) ? variant.getPrice() : product.getPrice();
        orderItem.setPrice(price);
        orderItem.setProductName(product.getName());
        orderItem.setVariantName(variant != null ? variant.getName() : null);
        
        String imageUrl = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrl = product.getImages().get(0).getImageUrl();
        }
        orderItem.setImageUrl(imageUrl);
        orderItem.setSku(variant != null ? variant.getSku() : product.getSku());
        
        return orderItem;
    }
}