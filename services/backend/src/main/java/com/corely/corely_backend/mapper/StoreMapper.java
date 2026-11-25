package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.store.StoreUpdateRequest;
import com.corely.corely_backend.dto.response.store.StoreResponse;
import com.corely.corely_backend.entity.Store;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StoreMapper {

    StoreResponse toStoreResponse(Store store);

    void updateStore(StoreUpdateRequest request, @MappingTarget Store store);
}
