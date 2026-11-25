package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.product.ProductVariantRequest;
import com.corely.corely_backend.dto.response.product.ProductVariantResponse;
import com.corely.corely_backend.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {
    @Mapping(target = "product", ignore = true)
    ProductVariant toProductVariant(ProductVariantRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateProductVariant(@MappingTarget ProductVariant entity, ProductVariantRequest request);

    ProductVariantResponse toProductVariantResponse(ProductVariant productVariant);
}
