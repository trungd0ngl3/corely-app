package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.product.BrandRequest;
import com.corely.corely_backend.dto.response.BrandResponse;
import com.corely.corely_backend.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand(BrandRequest request);
    BrandResponse toBrandResponse(Brand brand);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    void updateBrand(@MappingTarget Brand brand, BrandRequest request);
}
