package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.response.order.OrderItemResponse;
import com.corely.corely_backend.dto.response.order.OrderResponse;
import com.corely.corely_backend.entity.Order;
import com.corely.corely_backend.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "store.id", target = "storeId")
    @Mapping(source = "store.name", target = "storeName")
    @Mapping(source = "totalAmount", target = "totalAmount") // Ensure correct mapping
    OrderResponse toOrderResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "variant.id", target = "variantId")
    @Mapping(source = "variant.name", target = "variantName")
    @Mapping(target = "imageUrl", expression = "java(orderItem.getProduct().getImages() != null && !orderItem.getProduct().getImages().isEmpty() ? orderItem.getProduct().getImages().get(0).getImageUrl() : null)")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
