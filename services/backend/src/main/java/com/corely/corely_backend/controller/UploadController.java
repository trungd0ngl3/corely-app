package com.corely.corely_backend.controller;

import com.corely.corely_backend.dto.response.ApiResponse;
import com.corely.corely_backend.dto.response.upload.UploadResponse;
import com.corely.corely_backend.enums.UploadFolder;
import com.corely.corely_backend.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "GENERAL") UploadFolder folder
    ) {
        return ApiResponse.<UploadResponse>builder()
                .result(uploadService.uploadImage(file, folder))
                .build();
    }

    @DeleteMapping("/image/{publicId}")
    public ApiResponse<Void> deleteImage(@PathVariable String publicId) {
        uploadService.deleteImage(publicId);
        return ApiResponse.<Void>builder().build();
    }
}