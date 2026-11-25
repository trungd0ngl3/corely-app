package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.store.StoreUpdateRequest;
import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.store.StoreResponse;
import com.corely.corely_backend.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ApiResponse<StoreResponse> getStore() {
        return ApiResponse.<StoreResponse>builder()
                .result(storeService.getStore())
                .build();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<StoreResponse> updateStore(
            @RequestBody @Valid StoreUpdateRequest request) {

        return ApiResponse.<StoreResponse>builder()
                .result(storeService.updateStore(request))
                .build();
    }
}
