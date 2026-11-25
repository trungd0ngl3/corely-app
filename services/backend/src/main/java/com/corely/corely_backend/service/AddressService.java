package com.corely.corely_backend.service;

import com.corely.corely_backend.dto.request.user.AddressRequest;
import com.corely.corely_backend.dto.response.AddressResponse;
import com.corely.corely_backend.entity.Address;
import com.corely.corely_backend.entity.User;
import com.corely.corely_backend.exception.AppException;
import com.corely.corely_backend.exception.ErrorCode;
import com.corely.corely_backend.mapper.AddressMapper;
import com.corely.corely_backend.repository.AddressRepository;
import com.corely.corely_backend.repository.UserRepository;
import com.corely.corely_backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    AddressRepository addressRepository;
    UserRepository userRepository;
    AddressMapper addressMapper;
    SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses() {
        return addressRepository.findByUserId(getCurrentUser().getId()).stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        User user = getCurrentUser();
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.resetDefault(user.getId());
        }
        Address address = addressMapper.toAddress(request);
        address.setUser(user);
        return addressMapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(UUID id, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(id, getCurrentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (Boolean.TRUE.equals(request.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            addressRepository.resetDefault(getCurrentUser().getId());
        }

        addressMapper.updateAddress(address, request);
        return addressMapper.toAddressResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(UUID id) {
        Address address = addressRepository.findByIdAndUserId(id, getCurrentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
        addressRepository.delete(address);

        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserId(getCurrentUser().getId());
            if (!remaining.isEmpty()) {
                Address next = remaining.get(0);
                next.setIsDefault(true);
                addressRepository.save(next);
            }
        }
    }

    @Transactional
    public void setDefault(UUID id) {
        addressRepository.resetDefault(getCurrentUser().getId());
        Address address = addressRepository.findByIdAndUserId(id, getCurrentUser().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        address.setIsDefault(true);
        addressRepository.save(address);
    }

    private User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }
}
