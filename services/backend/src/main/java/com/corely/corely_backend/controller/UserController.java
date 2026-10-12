package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.request.auth.ChangePasswordRequest;
import com.corely.corely_backend.dto.request.auth.UpdateProfileRequest;
import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.auth.UserResponse;
import com.corely.corely_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/me")
    public ApiResponse<UserResponse> updateMyInfo(@RequestBody @Valid UpdateProfileRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMyInfo(request))
                .build();
    }

    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMyInfo() {
        userService.deleteMyAccount();
        return ApiResponse.<Void>builder().build();
    }

}
