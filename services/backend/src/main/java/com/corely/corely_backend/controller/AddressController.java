package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.user.AddressRequest;
import com.corely.corely_backend.dto.response.AddressResponse;
import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    AddressService addressService;

    @GetMapping
    public ApiResponse<List<AddressResponse>> getMyAddresses() {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getMyAddresses())
                .build();
    }

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody @Valid AddressRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AddressResponse> updateAddress(@PathVariable UUID id, @RequestBody @Valid AddressRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
        return ApiResponse.<Void>builder().build();
    }

    @PatchMapping("/{id}/default")
    public ApiResponse<Void> setDefault(@PathVariable UUID id) {
        addressService.setDefault(id);
        return ApiResponse.<Void>builder().build();
    }
}
