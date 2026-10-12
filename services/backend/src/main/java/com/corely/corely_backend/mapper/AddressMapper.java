package com.corely.corely_backend.mapper;

import com.corely.corely_backend.dto.request.user.AddressRequest;
import com.corely.corely_backend.dto.response.AddressResponse;
import com.corely.corely_backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressRequest request);
    AddressResponse toAddressResponse(Address address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateAddress(@MappingTarget Address address, AddressRequest request);
}
