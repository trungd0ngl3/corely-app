package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.product.CategoryRequest;
import com.corely.corely_backend.dto.response.CategoryResponse;
import com.corely.corely_backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "id", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}
