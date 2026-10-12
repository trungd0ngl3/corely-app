package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.response.WishlistResponse;
import com.corely.corely_backend.entity.Wishlist;
import com.corely.corely_backend.entity.WishlistItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface WishlistMapper {

    @Mapping(target = "items", source = "items")
    WishlistResponse toWishlistResponse(Wishlist wishlist);

    WishlistResponse.WishlistItemResponse toWishlistItemResponse(WishlistItem item);
}
