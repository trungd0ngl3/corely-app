package com.corely.corely_backend.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    int code;
    String message;
    T result;

    public static <T> ApiResponse<T> success(T result) {
        return  ApiResponse.<T>builder()
                .code(1000)
                .message("Success")
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> success() {
        return  ApiResponse.<T>builder()
                .code(1000)
                .message("Success")
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return  ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T result, String message) {
        return  ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .result(result)
                .build();
    }

}
